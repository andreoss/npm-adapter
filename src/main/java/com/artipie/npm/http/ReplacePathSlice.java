/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 artipie.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.artipie.npm.http;

import com.artipie.http.Response;
import com.artipie.http.Slice;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rq.RequestLineFrom;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.regex.Pattern;
import org.reactivestreams.Publisher;

/**
 * Slice handles routing paths. It removes predefined routing path and passes the rest part
 * to the underlying slice.
 *
 * @since 0.6
 */
public final class ReplacePathSlice implements Slice {
    /**
     * Routing path.
     */
    private final String path;

    /**
     * Underlying slice.
     */
    private final Slice original;

    /**
     * Ctor.
     * @param path Routing path ("/" for ROOT context)
     * @param original Underlying slice
     */
    public ReplacePathSlice(final String path, final Slice original) {
        this.path = path;
        this.original = original;
    }

    @Override
    public Response response(
        final String line,
        final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body) {
        final RequestLineFrom request = new RequestLineFrom(line);
        return this.original.response(
            new RequestLine(
                request.method().value(),
                String.format(
                    "/%s",
                    request.uri().getPath().replaceFirst(
                        String.format("%s/?", Pattern.quote(this.path)),
                        ""
                    )
                ),
                request.version()
            ).toString(),
            headers,
            body
        );
    }
}
