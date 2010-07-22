/**
 * 
 */
package uk.ac.horizon.ug.exploding.logs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.horizon.ug.exploding.clientapi.ClientController;

/**
 * @author cmg
 *
 */
public class LogsController {

	static Logger logger = Logger.getLogger(LogsController.class.getName());

	private String logDir;
	
	
	/**
	 * @return the logDir
	 */
	public String getLogDir() {
		return logDir;
	}

	/**
	 * @param logDir the logDir to set
	 */
	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}

	/** client upload
	 * @throws IOException */
    public ModelAndView upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String deviceID = request.getParameter("deviceID");
    	if (deviceID==null || deviceID.length()==0) {
    		logger.error("upload request with parameter 'deviceID' not specified");
    		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "deviceID not specified");
    		return null;
    	}
    	String path = request.getParameter("file");
    	if (path==null || path.length()==0) {
    		logger.error("upload request with parameter 'file' not specified");
    		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "file not specified");
    		return null;    		
    	}
    	long length = request.getContentLength();
    	if (length<0) {
    		logger.error("upload request for unknown length");
    		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "content length not specified");
    		return null;    		
    	}
    
    	if (logDir==null) {
    		logger.error("log directory not set in servlet configuration");
    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "log directory not configured in webapp");
    		return null;    		    		
    	}
    	File dir = new File(logDir);
    	if (!dir.exists() || !dir.isDirectory()) {
    		logger.error("Log directory does not exist: "+logDir);
    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "log directory does not exist on server");
    		return null;
    	}
    	// directory for device
    	File dev = new File(dir, toFileName(deviceID));
    	if (dev.mkdir()) 
    		logger.info("Creating device log directory "+dev);
    	if (!dev.exists()) {
    		logger.error("Cannot create device log directory "+dev);
    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "unable to write log on server");
    		return null;
    	}
    	// path
    	String pathEls [] = path.split("[/\\\\]");
    	if (pathEls.length==0) {
    		logger.error("Path had no elements: "+path);
    		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "path had no path elements: "+path);
    		return null;    		
    	}
    	String filename = pathEls[pathEls.length-1];
    	char lastChar = path.charAt(path.length()-1);
    	if (lastChar=='/' || lastChar=='\\' || filename.equals("..")) {
    		logger.error("Path is a directory: "+path);
    		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "path is a directory: "+path);
    		return null;    		    		
    	}
    	File pathDir = dev;
    	for (int i=0; i<pathEls.length-1; i++) {
    		String dirName = toFileName(pathEls[i]);
    		if (dirName.equals("..")) {
        		logger.error("Path contains ..: "+path);
        		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "path contains ..: "+path);
        		return null;    		    		    			
    		}
    		pathDir = new File(pathDir, dirName);
    		if (pathDir.mkdir())
    			logger.info("Created log directory "+pathDir);
    		if (!pathDir.exists()) {
        		logger.error("Cannot create device log directory "+pathDir);
        		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "unable to write log on server");
        		return null;    			
    		}
    	}
    	File tmpFile = File.createTempFile("upload_", ".tmp", pathDir);
    	logger.info("Uploading to "+tmpFile+" ("+length+" bytes)");
    	FileOutputStream fos = null;
    	try {
    		fos = new FileOutputStream(tmpFile);
    	}
    	catch (Exception e) {
    		logger.error("Creating tmp log file "+tmpFile+": "+e);
    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "unable to write log on server");
    		return null;    		    		    			    		
    	}
    	try {
    		InputStream is = request.getInputStream();
    		copyBytes(is, fos, length);
    		fos.close();
    		is.close();
    	}
    	catch (Exception e) {
    		logger.error("Uploading log file "+path+": "+e);
    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "unable to write log on server");
    		return null;    		    		    			    		
    	}
    	File file = new File(pathDir, filename);
    	if (file.exists()) {
    		logger.warn("Log file already exists: "+file);
    		File attic = new File(pathDir, ".archive");
    		if (attic.mkdir())
    			logger.info("Creating log directory "+attic);
    		File atticFile = new File(attic, ""+System.currentTimeMillis()+"_"+filename);
    		if (file.renameTo(atticFile))
    			logger.info("Moved existing log file "+file+" to "+atticFile);
    		else {
        		logger.error("Cannot move existing log file "+file+" to "+atticFile);
        		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "unable to write log on server");
        		return null;    			    			
    		}
    	}
    	if (tmpFile.renameTo(file)) {
    		logger.info("Uploaded "+path+" to "+file+" ("+length+" bytes)");
    		response.setStatus(HttpServletResponse.SC_OK);
    		PrintWriter pw = new PrintWriter(response.getWriter());
    		pw.println("Uploaded "+path+" to "+file+" ("+length+" bytes)");
    		pw.close();
    		return null;
    	}
    	logger.error("Unable to rename "+tmpFile+" to "+file);
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "unable to write log on server");
		return null;    			    			
    }

	private void copyBytes(InputStream is, FileOutputStream fos, long length) throws IOException {
		byte buf[] = new byte[100000];
		long count = 0;
		while(count<length) {
			int next = buf.length;
			if (length-count < next)
				next = (int)(length-count);			
			int cnt = is.read(buf, 0, next);
			logger.info("Read "+cnt+"/"+next+" bytes");
			if (cnt<=0) {
				throw new IOException("End of input after "+count+" bytes ("+cnt+")");
			}
			fos.write(buf, 0, cnt);
			count += cnt;
		}
		int cnt = is.read();
		if (cnt>=0)
			throw new IOException("Input too long - read "+(count+1)+" bytes");
	}

	private String toFileName(String s) {
		return URLEncoder.encode(s).replace('%', '_'); 
	}

	/** client list
	 * @throws IOException */
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	return null;
    }
}
