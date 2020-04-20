package com.servoy.plugins.crystalreports;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.servoy.j2db.util.Debug;

import com.servoy.j2db.dataprocessing.IFoundSet;
import com.servoy.j2db.dataprocessing.IRecord;
import com.servoy.j2db.persistence.RepositoryException;
import com.servoy.j2db.plugins.IClientPluginAccess;
import com.servoy.j2db.solutionmodel.ISMCalculation;
import com.servoy.j2db.solutionmodel.ISMDataSourceNode;
import com.servoy.plugins.crystalreports.ResultSetMetadataProvider;

public class ResultSetProvider implements ResultSet {
	
	private int currentIndex = 0;
	protected IFoundSet foundset;
	protected ResultSetMetadataProvider metadata;
	protected List<String> dataProviders;
	protected IClientPluginAccess servoy;
	
	public ResultSetProvider(IFoundSet foundset, IClientPluginAccess servoy) throws RepositoryException {
		this.foundset = foundset;
		this.servoy = servoy;
		this.dataProviders = new ArrayList<String>();
		addFoundSetDataProviders();
		metadata = new ResultSetMetadataProvider(this);
	}
	
	private boolean isValidRow() {
		return currentIndex >= 1 && currentIndex <= foundset.getSize();
	}
	
	private void addFoundSetDataProviders() throws RepositoryException{
		
		//	add all columns
		String[] colNames = servoy.getDatabaseManager().getTable(foundset.getDataSource()).getColumnNames();
		for (int i = 0; i < colNames.length; i++) {
			String colName = colNames[i];
			dataProviders.add(colName);
		}
		
		//	add all calculations
		ISMDataSourceNode dataSource = servoy.getSolutionModel().getDataSourceNode(foundset.getDataSource());
		ISMCalculation[] calculations = dataSource.getCalculations();
		for (int i = 0; i < calculations.length; i++) {
			ISMCalculation calc = calculations[i];
			dataProviders.add(calc.getName());
		}
	}
	
	private Object getValue(int index){
		
		String dataProvider = dataProviders.get(index - 1);
		return getValue(dataProvider);
	}
	
	private Object getValue(String dataProvider){	
		
		IRecord record = foundset.getRecord(currentIndex-1);
		if(record == null){
			return null;
		}
		return record.getValue(dataProvider);
	}
	
	
	/*************************
	 * Begin Override Methods
	 * ***********************
	 */
	
	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return null;
	}

	@Override
	public boolean absolute(int arg0) throws SQLException {
		if(arg0 < 0){
			throw new UnsupportedOperationException("Forward Only");
		}
		currentIndex = arg0;
		return isValidRow();
	}

	@Override
	public void afterLast() throws SQLException {
		currentIndex = foundset.getSize() + 1;
	}

	@Override
	public void beforeFirst() throws SQLException {
		currentIndex = 0;
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		
	}

	@Override
	public void clearWarnings() throws SQLException {
		
	}

	@Override
	public void close() throws SQLException {
		foundset = null;
	}

	@Override 
	public void deleteRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int findColumn(String arg0) throws SQLException {
		int index = dataProviders.indexOf(arg0);
		return index + 1;
	}

	@Override
	public boolean first() throws SQLException {
		currentIndex = 1;
		return isValidRow();
	}

	@Override
	public Array getArray(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Array getArray(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getAsciiStream(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getAsciiStream(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getBigDecimal(int arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return (BigDecimal)getValue(arg0);
	}

	@Override
	public BigDecimal getBigDecimal(String arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return (BigDecimal)getValue(arg0);
	}

	@Override
	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return (BigDecimal)getValue(arg0);
	}

	@Override
	public BigDecimal getBigDecimal(String arg0, int arg1) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return (BigDecimal)getValue(arg0);
	}

	@Override
	public InputStream getBinaryStream(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getBinaryStream(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Blob getBlob(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Blob getBlob(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getBoolean(int arg0) throws SQLException {
		Debug.trace("getBoolean called for: " + arg0);
		Object value = getValue(arg0);
		if(value == null){
			return false;
		}
		if(value instanceof Boolean) {
			return ((Boolean)value).booleanValue();
		}
		if(value instanceof String) {
			return Boolean.valueOf((String)value);
		}
		if(value instanceof Integer) {
			int intValue = ((Integer)value).intValue();
			return intValue == 1;
		}
		return false;
	}

	@Override
	public boolean getBoolean(String arg0) throws SQLException {
		Debug.trace("getBoolean called for: " + arg0);
		Object value = getValue(arg0);
		if(value == null){
			return false;
		}
		if(value instanceof Boolean) {
			return ((Boolean)value).booleanValue();
		}
		if(value instanceof String) {
			return Boolean.valueOf((String)value);
		}
		if(value instanceof Integer) {
			int intValue = ((Integer)value).intValue();
			return intValue == 1;
		}
		return false;
	}

	@Override
	public byte getByte(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte getByte(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getBytes(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getBytes(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader getCharacterStream(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader getCharacterStream(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Clob getClob(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Clob getClob(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getConcurrency() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCursorName() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getDate(int arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return Date.valueOf(value.toString());
	}

	@Override
	public Date getDate(String arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return Date.valueOf(value.toString());
	}

	@Override
	public Date getDate(int arg0, Calendar arg1) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return Date.valueOf(value.toString());
	}

	@Override
	public Date getDate(String arg0, Calendar arg1) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return Date.valueOf(value.toString());
	}

	@Override
	public double getDouble(int arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return 0;
		}
		return Double.valueOf(getValue(arg0).toString());
	}

	@Override
	public double getDouble(String arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return 0;
		}
		return Double.valueOf(getValue(arg0).toString());
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return FETCH_FORWARD;
	}

	@Override
	public int getFetchSize() throws SQLException {
		return 200;
	}

	@Override
	public float getFloat(int arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return 0;
		}
		return Float.valueOf(getValue(arg0).toString());
	}

	@Override
	public float getFloat(String arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return 0;
		}
		return Float.valueOf(getValue(arg0).toString());
	}

	@Override
	public int getHoldability() throws SQLException {
		return HOLD_CURSORS_OVER_COMMIT;
	}

	@Override
	public int getInt(int arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return 0;
		}
		return Integer.valueOf(getValue(arg0).toString());
	}

	@Override
	public int getInt(String arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return 0;
		}
		return Integer.valueOf(getValue(arg0).toString());
	}

	@Override
	public long getLong(int arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return 0;
		}
		return Long.valueOf(getValue(arg0).toString());
	}

	@Override
	public long getLong(String arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return 0;
		}
		return Long.valueOf(getValue(arg0).toString());
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return metadata;
	}

	@Override
	public Reader getNCharacterStream(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader getNCharacterStream(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NClob getNClob(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NClob getNClob(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNString(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNString(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getObject(int arg0) throws SQLException {
		return getValue(arg0);
	}

	@Override
	public Object getObject(String arg0) throws SQLException {
		return getValue(arg0);
	}

	@Override
	public Object getObject(int arg0, Map<String, Class<?>> arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getObject(String arg0, Map<String, Class<?>> arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T getObject(int arg0, Class<T> arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T getObject(String arg0, Class<T> arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Ref getRef(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Ref getRef(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getRow() throws SQLException {
		return currentIndex;
	}

	@Override
	public RowId getRowId(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RowId getRowId(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLXML getSQLXML(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLXML getSQLXML(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public short getShort(int arg0) throws SQLException {
		return Short.valueOf(getValue(arg0).toString());
	}

	@Override
	public short getShort(String arg0) throws SQLException {
		return Short.valueOf(getValue(arg0).toString());
	}

	@Override
	public Statement getStatement() throws SQLException {
		return null;
	}

	@Override
	public String getString(int arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return value.toString();
	}

	@Override
	public String getString(String arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return value.toString();
	}

	@Override
	public Time getTime(int arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return Time.valueOf(getValue(arg0).toString());
	}

	@Override
	public Time getTime(String arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return Time.valueOf(getValue(arg0).toString());
	}

	@Override
	public Time getTime(int arg0, Calendar arg1) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return Time.valueOf(getValue(arg0).toString());
	}

	@Override
	public Time getTime(String arg0, Calendar arg1) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return Time.valueOf(getValue(arg0).toString());
	}

	@Override
	public Timestamp getTimestamp(int arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
//		return Timestamp.valueOf(getValue(arg0).toString());
		return new Timestamp(((java.util.Date)value).getTime());
	}

	@Override
	public Timestamp getTimestamp(String arg0) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
//		return Timestamp.valueOf(getValue(arg0).toString());
		return new Timestamp(((java.util.Date)value).getTime());
	}

	@Override
	public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return Timestamp.valueOf(getValue(arg0).toString());
	}

	@Override
	public Timestamp getTimestamp(String arg0, Calendar arg1) throws SQLException {
		Object value = getValue(arg0);
		if(value == null){
			return null;
		}
		return Timestamp.valueOf(getValue(arg0).toString());
	}

	@Override
	public int getType() throws SQLException {
		return ResultSet.TYPE_FORWARD_ONLY;
	}

	@Override
	public URL getURL(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURL(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getUnicodeStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getUnicodeStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return currentIndex > foundset.getSize();
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return currentIndex < 1;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return foundset == null;
	}

	@Override
	public boolean isFirst() throws SQLException {
		return currentIndex == 1;
	}

	@Override
	public boolean isLast() throws SQLException {
		return isValidRow() && currentIndex == foundset.getSize();
	}

	@Override
	public boolean last() throws SQLException {
		currentIndex = foundset.getSize();
		return isValidRow();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean next() throws SQLException {
//		logger.warn("NEXT" + " [INDEX="+currentIndex+"]");
		currentIndex++;
		return isValidRow();
	}

	@Override
	public boolean previous() throws SQLException {
		currentIndex--;
		return currentIndex >= 1;
	}

	@Override
	public void refreshRow() throws SQLException {
		
	}

	@Override
	public boolean relative(int arg0) throws SQLException {
		currentIndex += arg0;
		return isValidRow();
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return false;
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return false;
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return false;
	}

	@Override
	public void setFetchDirection(int arg0) throws SQLException {
//		if(arg0 != FETCH_FORWARD){
//			throw new UnsupportedOperationException("Only fetch forward supported");
//		}
	}

	@Override
	public void setFetchSize(int arg0) throws SQLException {
		
	}

	@Override
	public void updateArray(int arg0, Array arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateArray(String arg0, Array arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(String arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(String arg0, InputStream arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(String arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBigDecimal(String arg0, BigDecimal arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(String arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(String arg0, InputStream arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(String arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(int arg0, Blob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(String arg0, Blob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(String arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(int arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(String arg0, InputStream arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBoolean(int arg0, boolean arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBoolean(String arg0, boolean arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateByte(int arg0, byte arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateByte(String arg0, byte arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBytes(int arg0, byte[] arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBytes(String arg0, byte[] arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(String arg0, Reader arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(String arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(int arg0, Clob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(String arg0, Clob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(String arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDate(int arg0, Date arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDate(String arg0, Date arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDouble(int arg0, double arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDouble(String arg0, double arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFloat(int arg0, float arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFloat(String arg0, float arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateInt(int arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateInt(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLong(int arg0, long arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLong(String arg0, long arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(String arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(int arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(String arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(String arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNString(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNull(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNull(String arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(int arg0, Object arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(String arg0, Object arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(int arg0, Object arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(String arg0, Object arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRef(int arg0, Ref arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRef(String arg0, Ref arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRowId(int arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRowId(String arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateShort(int arg0, short arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateShort(String arg0, short arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateString(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTime(int arg0, Time arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTime(String arg0, Time arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTimestamp(String arg0, Timestamp arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean wasNull() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
