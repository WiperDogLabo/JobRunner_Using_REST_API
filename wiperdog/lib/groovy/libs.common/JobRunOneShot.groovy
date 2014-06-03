import groovy.json.*;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import java.util.jar.JarFile;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.osgi.framework.BundleContext;
import org.wiperdog.bundleextractor.BundleExtractor
import org.osgi.framework.ServiceReference
import org.osgi.util.tracker.ServiceTracker
import org.osgi.util.tracker.ServiceTrackerCustomizer

class JobRunOneShot{
	def properties = MonitorJobConfigLoader.getProperties()
	BundleContext ctx;
	JsonSlurper sluper = new JsonSlurper()
	def jobResult
	JobRunOneShot(BundleContext ctx){
		this.ctx = ctx;
	}
	public Object create(Request request, Response response){
		def listData = []
		request.addHeader("Access-Control-Allow-Origin", "*")
		response.addHeader("Access-Control-Allow-Origin", "*")
		response.addHeader("Content-type", "application/json")
		//Get list file from xwiki
		def dataReq =  (new ChannelBufferInputStream(request.getBody())).getText()
		def objectData = sluper.parseText(dataReq)
		def jobFilePath 
		def schedule 

		if(objectData != null ) {
			if(objectData.job != null) {
				jobFilePath = objectData.job
			} else {
				println "Missing job file path"
				return null
			}
			schedule = objectData.schedule
		}

		if(jobFilePath != null && !jobFilePath.equals("")) {

		}
		def jobRunnerMain
		jobResult = null
		while(jobRunnerMain == null ) {
			jobRunnerMain = ctx.getService(ctx.getServiceReference("JobRunnerMainService"))	
			if(jobRunnerMain != null ) {
				jobRunnerMain.removeJob(jobFilePath)
				jobRunnerMain.executeJob(jobFilePath,schedule)
			}
		}
		//Set timeout to waiting for job result in 60s
		def timeout = 60000 
		def expirationTime = System.currentTimeMillis() + timeout
		def currentTime = System.currentTimeMillis()
		while(jobResult == null && currentTime < expirationTime) {
			currentTime = System.currentTimeMillis()
			Thread.sleep(1000)
		}
		return jobResult
	}
	public String update(Request request, Response response){

		request.addHeader("Access-Control-Allow-Origin", "*")
		response.addHeader("Access-Control-Allow-Origin", "*")
		def responseData = [:]
		def dataReq =  (new ChannelBufferInputStream(request.getBody())).getText()
		def objectData = sluper.parseText(dataReq)		
		def builder = new JsonBuilder(objectData)
		jobResult = builder.toPrettyString()
		return null
	}

	// get  /runjob/data
	public Map<String,Object> read(Request request, Response response){
		request.addHeader("Access-Control-Allow-Origin", "*")
		response.addHeader("Access-Control-Allow-Origin", "*")
		def responseData = [:]
		def dataReq =  (new ChannelBufferInputStream(request.getBody())).getText()
		def objectData = sluper.parseText(dataReq)
		return objectData
	}
	public String delete(Request request, Response response){
		println "delete"
		return "delete_METHOD"
	}
	
}