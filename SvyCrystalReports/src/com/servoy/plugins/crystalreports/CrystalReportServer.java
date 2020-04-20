/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * Servoy - Smart Technology For Smart Clients.
 * Copyright � 1997-2016 Servoy BV http://www.servoy.com
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 * 
 * Servoy B.V.
 * De Brand 26
 * 3823 LJ Amersfoort
 * The Netherlands
 * http://www.servoy.com
 */
package com.servoy.plugins.crystalreports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.servoy.j2db.server.main.ApplicationServer;
import com.servoy.j2db.plugins.IServerAccess;
import com.servoy.j2db.plugins.IServerPlugin;
import com.servoy.j2db.plugins.PluginException;
import com.servoy.j2db.util.Debug;

public class CrystalReportServer implements ICrystalReportServer, IServerPlugin {

	public final static String REPORTS_DIRECTORY = "directory.crystal.report";
	private IServerAccess servoy;
	
	
	/************************
	 * Server plug-in methods
	 ************************/
	
	@Override
	public Properties getProperties() {
		Properties properties = new Properties();
		properties.put(DISPLAY_NAME, "CrystalReportsPlugin");
		return properties;
	}

	@Override
	public void load() throws PluginException {
		
	}

	@Override
	public void unload() throws PluginException {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> getRequiredPropertyNames() {
		Map<String, String> req = new HashMap<String, String>();
		req.put(REPORTS_DIRECTORY, "Reports Directory");
		return req;
	}

	@Override
	public void initialize(IServerAccess servoy) throws PluginException {
		this.servoy = servoy;
		
		try {
			servoy.registerRemoteService(SERVICE_NAME, this);
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}

	/************************
	 * Report Server methods
	 ************************/
	
	@Override
	public byte[] getReport(String clientId, String report) throws RemoteException {
		try {
			String reportDirectory = getReportDirectory();
			String path = reportDirectory + (reportDirectory.endsWith("/") ? "" : "/")+ report;
			File reportFile = new File(path);
			if(reportFile.exists() && reportFile.canRead()){
				return Files.readAllBytes(reportFile.toPath());
			} else {
				throw new IllegalStateException("Cannot read report file: " + path);
			}
		} catch (Exception e) {
			Debug.error("Could not load report file: " + report);
			throw new RemoteException("Could not get report file: " + report, e);
		}
	}

	@Override
	public String[] getReportNames() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/************************
	 * Privte methods
	 ************************/
	
	
	/**
	 * Getting the plugin's reports directory which has been set from the AdminServer page.
	 * 
	 * @return the path to the reports' directory
	 */
	private String getReportDirectory() throws Exception {
		String repDir = servoy.getSettings().getProperty(REPORTS_DIRECTORY);
		if (repDir == null)
		{
			//default to appserver_dir/server/reports
			// TODO Default reports dir ???
			Debug.log("Report directory has not being set. Default location will be now set to /application_server/server/reports");
			try
			{
				String appServerDir = adjustFile(ApplicationServer.getInstance().getServoyApplicationServerDirectory());
				if (appServerDir != null)
				{
					repDir = absolutePathFormatting(appServerDir + (appServerDir.endsWith("/") ? "" : (appServerDir.endsWith("\\") ? "" : "/" )) + "server/reports");
					setReportDirectory(repDir);
					File f = new File(repDir);
					if (!f.exists())
					{
						f.createNewFile();	
					}
				}
			}
			catch (Exception ex)
			{
				Debug.error("Exception encountered while setting default report directory: " + ex.getMessage());
				return null;
			}
		}
		//safety
		return absolutePathFormatting(repDir);
	}
	
	private static String adjustFile(String file) {
		/* if (java.io.File.separator == "/") */
		if (!System.getProperty("os.name").startsWith("Windows")) {
			while (file.indexOf('\\') != -1) {
				file = file.replace('\\', '/');
			}
		} else {
			while (file.indexOf('/') != -1) {
				file = file.replace('/', '\\');
			}
		}
		return file;
	}
	
	private static String absolutePathFormatting(String path) throws IOException
	{
		if (System.getProperty("os.name").startsWith("Windows"))
		{
			File f = new File(path);
			if (f.isAbsolute()) return f.getCanonicalPath();  
		}
		return path;
	}
	
	/**
	 * Setting the (absolute) server-side path to the plugin's reports directory 
	 * 
	 * @param jasperDirectory absolute path to the plugin's reports directory on the server
	 */
	private void setReportDirectory(String jasperDirectory) {
		servoy.getSettings().setProperty("directory.jasper.report", adjustFile(jasperDirectory));
	}
}
