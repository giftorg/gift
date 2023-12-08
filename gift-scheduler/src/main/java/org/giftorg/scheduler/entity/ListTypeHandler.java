/**
 * Copyright 2023 GiftOrg Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.giftorg.scheduler.entity;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * MyBatis List 类型处理器
 */
public class ListTypeHandler implements TypeHandler<List<String>> {
    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setString(i, null);
            return;
        }
        ps.setString(i, String.join(" ", parameter));
    }

    @Override
    public List<String> getResult(java.sql.ResultSet rs, String columnName) throws java.sql.SQLException {
        if (rs.getString(columnName) == null) {
            return null;
        }
        return java.util.Arrays.asList(rs.getString(columnName).split(" "));
    }

    @Override
    public List<String> getResult(java.sql.ResultSet rs, int columnIndex) throws java.sql.SQLException {
        if (rs.getString(columnIndex) == null) {
            return null;
        }
        return java.util.Arrays.asList(rs.getString(columnIndex).split(" "));
    }

    @Override
    public List<String> getResult(java.sql.CallableStatement cs, int columnIndex) throws java.sql.SQLException {
        if (cs.getString(columnIndex) == null) {
            return null;
        }
        return java.util.Arrays.asList(cs.getString(columnIndex).split(" "));
    }
}
