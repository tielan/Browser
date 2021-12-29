package com.chinacreator.browser.body;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.IOUtils;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class StringBody implements ResponseBody {

    private byte[] mBody;
    private MediaType mMediaType;

    public StringBody(String body) {
        this(body, MediaType.TEXT_PLAIN);
    }

    public StringBody(String body, MediaType mediaType) {
        if (body == null) {
            throw new IllegalArgumentException("The content cannot be null.");
        }

        this.mMediaType = mediaType;
        if (mMediaType == null) {
            mMediaType = new MediaType(MediaType.TEXT_PLAIN,Charset.forName("UTF-8"));
        }

        Charset charset = mMediaType.getCharset();
        if (charset == null) {
            charset = Charset.forName("UTF-8");
        }
        this.mBody = body.getBytes(charset);
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public long contentLength() {
        return mBody.length;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        Charset charset = mMediaType.getCharset();
        if (charset == null) {
            charset = Charset.forName("UTF-8");
            return new MediaType(mMediaType.getType(), mMediaType.getSubtype(), charset);
        }
        return mMediaType;
    }

    @Override
    public void writeTo(@NonNull OutputStream output) throws IOException {
        IOUtils.write(output, mBody);
    }
}