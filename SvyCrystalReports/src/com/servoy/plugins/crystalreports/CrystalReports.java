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

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.crystaldecisions.sdk.occa.report.application.DatabaseController;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat;
import com.servoy.j2db.plugins.IClientPluginAccess;

public class CrystalReports {

	static IClientPluginAccess app;
	public static void main(String[] args){
		
		try {
			
		//you will need to modify report variable to point to your report
//		String report = "C:/Servoy/Internal/BAP/CrystalReports/JRC_Examples/CR11_JRC_ExportWithParameters/CR11_JRC_ExportWithParameters.rpt";
//		String report = "C:/Servoy/Internal/BAP/CrystalReports/JRC_Examples/Print_to_PDF/jrc_export_report.rpt";
		String report = "C:/Servoy/Internal/BAP/CrystalReports/Report1.rpt";
//		String report = "C:/Servoy/Internal/BAP/CrystalReports/JRC_Examples/jrc_resultset_datasource/jrc_resultset_datasource.rpt";
		
		//Open report.
		ReportClientDocument reportClientDoc = new ReportClientDocument();
		reportClientDoc.open(report, 0);
		
		DatabaseController databaseController = reportClientDoc.getDatabaseController();
		ResultSetProvider rs = new ResultSetProvider(null, null);
		databaseController.setDataSource(rs, "customers", "customers");
		
		
		
		
//		Tables tables = databaseController.getDatabase().getTables();
//		for (Iterator i = tables.iterator(); i.hasNext();) {
//			ITable table = (ITable) i.next();
//			Fields<IField> fields = table.getDataFields();
//			System.out.println(table.getName());
//			for (Iterator j = fields.iterator(); j.hasNext();) {
//				IField field = (IField) j.next();
//				System.out.println(field.getName());
//
//			}
//			
//		}
		
		
//		String url = "jdbc:postgresql://localhost/example";
//		Properties props = new Properties();
//		props.setProperty("user","DBA");
//		props.setProperty("password","");
//		java.sql.Connection conn = DriverManager.getConnection(url, props);
//		String sql = "SELECT companyname, city FROM customers";
//		PreparedStatement ps = conn.prepareStatement(sql);
//		if(!ps.execute()){
//			throw new Exception("Failed SQL");
//		}
//		ResultSet rs = ps.getResultSet();
//		databaseController.setDataSource(rs, "customers", "customers");
		
		
		
//		DataSet ds = new DataSet();
//		Tables tables = new Tables();
//		Table table = new Table();
//		table.setName("customers");
//		
//		Fields<IField> fields = new Fields<IField>();
//		
//		Field companyname = new DBField();
//		companyname.setName("copmanyname");
//		companyname.setType(FieldValueType.stringField);
//		fields.add(companyname);
//		
//		Field city = new DBField();
//		city.setName("copmanyname");
//		city.setType(FieldValueType.stringField);
//		fields.add(city);
//		
//		table.setDataFields(fields);
//		tables.add(table);
//		ds.setTables(tables);
//		
//		PropertyBag info = databaseController.getConnectionInfos(null).getConnectionInfo(0).getAttributes();
//		for (Iterator<Object> i = info.keySet().iterator(); i.hasNext();) {
//			Object key = i.next();
//			Object value = info.get(key);
//			System.out.println(key + ": " + value);
//		}
		
//		databaseController.setDataSource(ds);
		
//		ResultSet resultSet = new ResultSetProvider(null);
//		String tableAlias = databaseController.getDatabase().getTables().getTable(0).getAlias();
//		databaseController.setDataSource(resultSet, tableAlias , "resultsetTable");
		
		//NOTE: If parameters or database login credentials are required, they need to be set before.
		//calling the export() method of the PrintOutputController.
//		ParameterFieldController paramController = reportClientDoc.getDataDefController().getParameterFieldController();
//		paramController.setCurrentValue("","testParameter","FOOBAR");
		
		
//		IDataDefinition dataDef = reportClientDoc.getDataDefinition();
//		Fields<IFormulaField> formulaFields = dataDef.getFormulaFields();
//		FormulaFieldController formulaFieldController = reportClientDoc.getDataDefController().getFormulaFieldController();
//		for (Iterator<IFormulaField> i = formulaFields.iterator(); i.hasNext();) {
//			IFormulaField oldField = i.next();
//			
//			FormulaField newField = new FormulaField();
//			newField.setName(oldField.getName());
//			newField.setText("FOOOOLIO!");
//			newField.setSyntax(FormulaSyntax.crystal);
//			formulaFieldController.modify(oldField, newField);
//		}
		
		//Export report and obtain an input stream that can be written to disk.
		//See the Java Reporting Component Developer's Guide for more information on the supported export format enumerations
		//possible with the JRC.
		ByteArrayInputStream byteArrayInputStream = (ByteArrayInputStream)reportClientDoc.getPrintOutputController().export(ReportExportFormat.PDF);
				
		//Release report.
		reportClientDoc.close();
	
		
		//Write file to disk...
		String EXPORT_OUTPUT = "c:/servoy/internal/bap/CrystalReports/output.pdf";
		System.out.println("Exporting to " + EXPORT_OUTPUT);
		writeToFileSystem(byteArrayInputStream, EXPORT_OUTPUT);
		
		if(Desktop.isDesktopSupported())
		{
		  Desktop.getDesktop().browse(new File(EXPORT_OUTPUT).toURI());
		}
		
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	* Utility method that demonstrates how to write an input stream to the server's local file system.  
	*/
	private static void writeToFileSystem(ByteArrayInputStream byteArrayInputStream, String exportFile) throws Exception {
	
		//Use the Java I/O libraries to write the exported content to the file system.
		byte byteArray[] = new byte[byteArrayInputStream.available()];

		//Create a new file that will contain the exported result.
		File file = new File(exportFile);
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(byteArrayInputStream.available());
		int x = byteArrayInputStream.read(byteArray, 0, byteArrayInputStream.available());

		byteArrayOutputStream.write(byteArray, 0, x);
		byteArrayOutputStream.writeTo(fileOutputStream);

		//Close streams.
		byteArrayInputStream.close();
		byteArrayOutputStream.close();
		fileOutputStream.close();
		
	}
}
	