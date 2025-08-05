using System;
using System.Data;
using System.Data.Common;
using System.IO;
using System.Xml.Serialization;
using System.Collections.Generic;
using LibUtil.Security;
using System.Data.Odbc;
using System.Data.SqlClient;

namespace LibUtil.Data
{
    public class ConnectionManager
    {
        public static IDbConnection GetConnection(string ConnectionString)
        {

            if (ConnectionString.ToLower().Contains("dsn"))
            {
                return new OdbcConnection(ConnectionString);
            }
            else
            {
                return new SqlConnection(ConnectionString);
            }
        }
    }

}
