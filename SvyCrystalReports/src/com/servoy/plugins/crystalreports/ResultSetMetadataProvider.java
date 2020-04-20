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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import com.servoy.j2db.persistence.ITable;
import com.servoy.j2db.persistence.RepositoryException;
import com.servoy.j2db.solutionmodel.ISMCalculation;
import com.servoy.j2db.solutionmodel.ISMDataSourceNode;

public class ResultSetMetadataProvider implements ResultSetMetaData {


	private ResultSetProvider resultSet;
	
	public ResultSetMetadataProvider(ResultSetProvider resultSet) {
		this.resultSet = resultSet;
	}
	
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public String getCatalogName(int arg0) throws SQLException {
		return null;
	}

	@Override
	public String getColumnClassName(int arg0) throws SQLException {
		
		return null;
	}

	@Override
	public int getColumnCount() throws SQLException {
		return resultSet.dataProviders.size();
	}

	@Override
	public int getColumnDisplaySize(int arg0) throws SQLException {
		return 255;
//		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public String getColumnLabel(int arg0) throws SQLException {
		return resultSet.dataProviders.get(arg0 - 1);
	}

	@Override
	public String getColumnName(int arg0) throws SQLException {
 		return resultSet.dataProviders.get(arg0 - 1);
	}

	@Override
	public int getColumnType(int arg0) throws SQLException {
		try {
			String colName = getColumnName(arg0);
			
			//	search calcs
			ISMDataSourceNode dataSource = resultSet.servoy.getSolutionModel().getDataSourceNode(resultSet.foundset.getDataSource());
			ISMCalculation calc = dataSource.getCalculation(colName);
			if(calc != null){
				return calc.getVariableType();
			}
			
			//	Assume table columns
			ITable table = resultSet.servoy.getDatabaseManager().getTable(resultSet.foundset.getDataSource());
			return table.getColumnType(colName);
			
		} catch (RepositoryException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public String getColumnTypeName(int arg0) throws SQLException {
		// TODO SQL Type Mapping
		return null;
	}

	@Override
	public int getPrecision(int arg0) throws SQLException {
		return 0;
	}

	@Override
	public int getScale(int arg0) throws SQLException {
		return 0;
	}

	@Override
	public String getSchemaName(int arg0) throws SQLException {
		return null;
	}

	@Override
	public String getTableName(int arg0) throws SQLException {
		
		// TODO support for related
		ITable table;
		try {
			// TODO SQL Type Mapping
			table = resultSet.servoy.getDatabaseManager().getTable(resultSet.foundset.getDataSource());
			return table.getName();
		} catch (RepositoryException e) {
			// TODO LOG
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isAutoIncrement(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean isCaseSensitive(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean isCurrency(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean isDefinitelyWritable(int arg0) throws SQLException {
		return false;
	}

	@Override
	public int isNullable(int arg0) throws SQLException {
		return 0;
	}

	@Override
	public boolean isReadOnly(int arg0) throws SQLException {
		return true;
	}

	@Override
	public boolean isSearchable(int arg0) throws SQLException {
		return true;
	}

	@Override
	public boolean isSigned(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean isWritable(int arg0) throws SQLException {
		return false;
	}

}
