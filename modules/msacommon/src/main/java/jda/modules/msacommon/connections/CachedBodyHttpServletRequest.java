package jda.modules.msacommon.connections;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.util.StreamUtils;


public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request, byte[] requestBody) throws IOException {
        super(request);
        this.cachedBody = requestBody;
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyServletInputStream(this.cachedBody);
    }
    
    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
    
    public class CachedBodyServletInputStream extends ServletInputStream {

        private InputStream cachedBodyInputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished(){
            try {
				return cachedBodyInputStream.available() == 0;
			} catch (IOException e) {
				return false;
			}
        }

        @Override
        public boolean isReady() {
            return true;
        }

		@Override
		public void setReadListener(ReadListener listener) {
		}

		@Override
		public int read() throws IOException {
		    return cachedBodyInputStream.read();
		}
    }
}
