package com.ai.paas.ipaas.i18n;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Date.class)
public class GMTTimeStampHandler extends BaseTypeHandler<Date> {
	private Calendar gmtCalendar = Calendar.getInstance(TimeZone
			.getTimeZone("GMT"));

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			Date parameter, JdbcType jdbcType) throws SQLException {
		// 此处需要转换成UTC时间，而不管传输进来的时间是那个时区的
		// 如果传进来的已经是UTC呢，需要进行判断
		if (parameter == null)
			ps.setNull(i, Types.TIMESTAMP);
		else {
			ps.setDate(i, new java.sql.Date(parameter.getTime()), gmtCalendar);
		}
	}

	@Override
	public Date getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		// 继续返回UTC时间
		return rs.getDate(columnName);
	}

	@Override
	public Date getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return rs.getDate(columnIndex);
	}

	@Override
	public Date getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return cs.getTime(columnIndex);
	}

}