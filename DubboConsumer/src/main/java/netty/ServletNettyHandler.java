package netty;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

public class ServletNettyHandler extends ChannelInboundMessageHandlerAdapter<HttpRequest> {

	private final Servlet servlet;

	private final ServletContext servletContext;

	public ServletNettyHandler(Servlet servlet) {
		this.servlet = servlet;
		this.servletContext = servlet.getServletConfig().getServletContext();
	}

	@Override
    public void messageReceived(ChannelHandlerContext ctx, HttpRequest request) throws Exception {

        if (!request.getDecoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

		MockHttpServletRequest servletRequest = createServletRequest(request);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

		this.servlet.service(servletRequest, servletResponse);

        HttpResponseStatus status = HttpResponseStatus.valueOf(servletResponse.getStatus());
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);

		for (String name : servletResponse.getHeaderNames()) {
			for (Object value : servletResponse.getHeaderValues(name)) {
				response.addHeader(name, value);
			}
		}

        // Write the initial line and the header.
        ctx.write(response);

        InputStream contentStream = new ByteArrayInputStream(servletResponse.getContentAsByteArray());

		// Write the content.
        ChannelFuture writeFuture = ctx.write(new ChunkedStream(contentStream));
        writeFuture.addListener(ChannelFutureListener.CLOSE);
    }

	private MockHttpServletRequest createServletRequest(HttpRequest httpRequest) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(httpRequest.getUri()).build();

		MockHttpServletRequest servletRequest = new MockHttpServletRequest(this.servletContext);
		servletRequest.setRequestURI(uriComponents.getPath());
		servletRequest.setPathInfo(uriComponents.getPath());
		servletRequest.setMethod(httpRequest.getMethod().getName());

		if (uriComponents.getScheme() != null) {
			servletRequest.setScheme(uriComponents.getScheme());
		}
		if (uriComponents.getHost() != null) {
			servletRequest.setServerName(uriComponents.getHost());
		}
		if (uriComponents.getPort() != -1) {
			servletRequest.setServerPort(uriComponents.getPort());
		}

		for (String name : httpRequest.getHeaderNames()) {
			for (String value : httpRequest.getHeaders(name)) {
				servletRequest.addHeader(name, value);
			}
		}

		servletRequest.setContent(httpRequest.getContent().array());

		try {
			if (uriComponents.getQuery() != null) {
				String query = UriUtils.decode(uriComponents.getQuery(), "UTF-8");
				servletRequest.setQueryString(query);
			}

			for (Entry<String, List<String>> entry : uriComponents.getQueryParams().entrySet()) {
				for (String value : entry.getValue()) {
					servletRequest.addParameter(
							UriUtils.decode(entry.getKey(), "UTF-8"),
							UriUtils.decode(value, "UTF-8"));
				}
			}
		}
		catch (UnsupportedEncodingException ex) {
			// shouldn't happen
		}

		return servletRequest;
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(Unpooled.copiedBuffer(
                "Failure: " + status.toString() + "\r\n",
                CharsetUtil.UTF_8));

        // Close the connection as soon as the error message is sent.
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }

}
