package com.xiuGEN.fliter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiuGEN.controller.UserController;
import org.apache.http.HttpHeaders;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 *
 * 将请求页面和图片进行压缩处理
 *
 */
@Component
public class CompressionFilter implements Filter {

    private final int THRESHOLD = 1024;
    private final List<String> SUPPORTED_MEDIA_TYPES = Arrays.asList("image/jpeg", "image/png","image/jpg");
    private final List<String> NOTSUPPORTED_PATH = Arrays.asList("/download");
    private static Logger logger = LoggerFactory.getLogger(CompressionFilter.class);
    @Value("${isEnableCompress}")
    private String flag;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getMethod().equalsIgnoreCase(HttpMethod.GET.name())
                && StringUtils.startsWithIgnoreCase(httpRequest.getHeader(HttpHeaders.ACCEPT_ENCODING), "gzip") && Boolean.parseBoolean(flag)) {

            ContentCachingResponseWrapper cachingResponseWrapper = new ContentCachingResponseWrapper(httpResponse);
            chain.doFilter(request, cachingResponseWrapper);
            byte[] responseBytes = cachingResponseWrapper.getContentAsByteArray();
            String contentType = cachingResponseWrapper.getContentType();
            if (responseBytes.length > THRESHOLD) {
                byte[] compressedBytes =null;
                //对图片进行压缩
                if (StringUtils.hasText(contentType) && SUPPORTED_MEDIA_TYPES.contains(contentType) && !NOTSUPPORTED_PATH.contains(((HttpServletRequest) request).getServletPath())){
                    try {
                        compressedBytes = compressImage(responseBytes,cachingResponseWrapper.getContentType(), 0.1F);
                    }catch (UnsupportedOperationException e){
                        logger.error("该图片型不支持压缩："+e.getMessage());
                        //对文件进行压缩
                        compressedBytes = compress(responseBytes);
                        httpResponse.setHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
                    }
                }else {
                    //对文件进行压缩
                    compressedBytes = compress(responseBytes);
                    httpResponse.setHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
                }
                httpResponse.setContentLength(compressedBytes.length);
                httpResponse.getOutputStream().write(compressedBytes);
            } else {
                httpResponse.setContentLength(responseBytes.length);
                httpResponse.getOutputStream().write(responseBytes);
            }

            cachingResponseWrapper.copyBodyToResponse();
        } else {
            chain.doFilter(request, response);
        }
    }

    private byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }
    /**
     *
     * compressImage()方法接收三个参数：要压缩的图片文件、压缩后的图片格式和压缩质量。其中，压缩质量的范围是0.0f到1.0f之间，数值越小，压缩率越高，但图片质量也越低。
     */
    private   byte[] compressImage(byte[] data, String formatName, float quality) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        // 读取原始图片
        BufferedImage originalImage = ImageIO.read(inputStream);
        // 创建一个用于存储压缩后图片数据的输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        formatName = formatName.substring(formatName.indexOf("/")+1,formatName.length());
        // 获取图片编码器
        ImageWriter writer = ImageIO.getImageWritersByFormatName(formatName).next();
        // 配置压缩参数
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(quality);

        // 将压缩后的图片写入到输出流中
        writer.setOutput(new MemoryCacheImageOutputStream(outputStream));
        writer.write(null, new IIOImage(originalImage, null, null), writeParam);

        // 获取压缩后的图片字节数组并返回
        byte[] compressedImageData = outputStream.toByteArray();
        outputStream.close();
        return compressedImageData;
    }

    public static void main(String[] args) {
        int a = 0;
        try {
            test();
        }catch (Exception e){
            System.out.println("异常被捕后");
        }
        System.out.println("异常捕获后继续运行");
    }
    public static  void test() throws Exception{
        throw  new RuntimeException();
    }
}
