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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.Iterator;

import com.crystaldecisions.sdk.occa.report.application.DatabaseController;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.data.Fields;
import com.crystaldecisions.sdk.occa.report.data.IFormulaField;
import com.crystaldecisions.sdk.occa.report.document.IReportOptions;
import com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat;
import com.crystaldecisions.sdk.occa.report.data.FormulaField;
import com.servoy.j2db.dataprocessing.IFoundSet;
import com.servoy.j2db.plugins.IClientPluginAccess;
import com.servoy.j2db.scripting.IReturnedTypesProvider;
import com.servoy.j2db.scripting.IScriptable;

public class CrystalReportsPluginProvider implements IScriptable, IReturnedTypesProvider {

	private IClientPluginAccess servoy;

	public CrystalReportsPluginProvider(IClientPluginAccess servoy){
		this.servoy = servoy;
	}
	public byte[] js_runReport(String reportPath, IFoundSet foundset) throws Exception {
		return js_runReport(reportPath, foundset, OUTPUT_FORMAT.PDF);
	}
	public byte[] js_runReport(String reportPath, IFoundSet foundset, String format) throws Exception{
		return js_runReport(reportPath, foundset, format, null);
	}
	public byte[] js_runReport(String reportPath, IFoundSet foundset, String format, FormulaFieldSubstitutions formulaFieldSubstitutions) throws Exception {
		
		ReportClientDocument reportClientDoc = new ReportClientDocument();
		try{
			
			//	Load Report
			File reportFile = getReportFileLocal(reportPath);
			reportClientDoc.open(reportFile.getAbsolutePath(), 0);
			
			//	 substitute formula fields
			if(formulaFieldSubstitutions != null){
				Fields<IFormulaField> formulaFields = reportClientDoc.getDataDefinition().getFormulaFields();
				
				for (Iterator<IFormulaField> i = formulaFields.iterator(); i.hasNext();) {
					IFormulaField iFormulaField = i.next();
					String fieldName = iFormulaField.getName();
					
					//	look for substitution
					String formulaText = formulaFieldSubstitutions.get(fieldName);
					if(formulaText != null){
						
						FormulaField newField;
						newField = new FormulaField();
						newField.setName(fieldName);
						newField.setText(formulaText);
						newField.setSyntax( iFormulaField.getSyntax());
						
						reportClientDoc.getDataDefController().getFormulaFieldController().modify(iFormulaField, newField);
					}
					// save
//					reportClientDoc.saveAs("temp_report.xml", "c:/servoy/internal/bap/crystalreports", ReportSaveAsOptions.overwriteExisting.value());
				}
				IReportOptions reportOptions = reportClientDoc.getReportOptions();
				reportOptions.setEnableSaveSummariesWithReport(true);
				reportOptions.setEnableSaveDataWithReport(true);
				reportClientDoc.getDataDefinition().setFormulaFields(formulaFields);
			}
			
			
			//	Substitute Dynamic Dataset
			DatabaseController databaseController = reportClientDoc.getDatabaseController();
			ResultSet resultSet = new ResultSetProvider(foundset, servoy);
			databaseController.setDataSource(resultSet, null, null);
			
			
//			reportClientDoc.close();

			
			//	Print Format
			ByteArrayInputStream is = 
					(ByteArrayInputStream)reportClientDoc.getPrintOutputController().export(getFormat(format));
			return getBytesFromInputStream(is);			
		
		}catch(Exception e){
			e.printStackTrace();
			reportClientDoc.close();
			throw e;
		}
	}
	
	public FormulaFieldSubstitutions js_createFormulaFieldSubstitutions(){
		return new FormulaFieldSubstitutions();
	}
	
	private static ReportExportFormat getFormat(String format){
		switch (format) {
			case OUTPUT_FORMAT.PDF:
				return ReportExportFormat.PDF;
			case OUTPUT_FORMAT.RTF:
				return ReportExportFormat.RTF;
			case OUTPUT_FORMAT.CSV:
				return ReportExportFormat.characterSeparatedValues;
			default:
				return ReportExportFormat.PDF;
		}
	}
	
	private static byte[] getBytesFromInputStream(InputStream is) throws IOException
	{
	    try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
	    {
	        byte[] buffer = new byte[0xFFFF];

	        for (int len; (len = is.read(buffer)) != -1;)
	            os.write(buffer, 0, len);

	        os.flush();

	        return os.toByteArray();
	    }
	}
	
	private File getReportFileLocal(String reportPath) throws RemoteException, Exception{
		byte[] reportBytes = getReportServer().getReport(servoy.getClientID(), reportPath);
		File file = File.createTempFile("crystal_report", ".rpt");	
		Files.write(file.toPath(), reportBytes);
		return file;
	}
	
	private ICrystalReportServer getReportServer() throws Exception{
		ICrystalReportServer server = (ICrystalReportServer) servoy.getRemoteService(ICrystalReportServer.SERVICE_NAME);
		if(server == null){
			throw new IllegalStateException("Could not load remote service");
		}
		return server;
	}
	

	@Override
	public Class<?>[] getAllReturnedTypes() {
		return new Class[] {
			OUTPUT_FORMAT.class,
			FormulaFieldSubstitutions.class
		};
	}
}
