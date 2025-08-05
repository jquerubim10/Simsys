using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using LibUtil.Data;
using Microsoft.Extensions.Configuration;

namespace SyncStock
{
    public class AppConfig
    {
        public static string ConnectionType { get; private set; }
        public static string ConnectionString { get; private set;  }
        public static string DbcFile { get; internal set; }

        public static void ReadConfigurationFile(string filename)
        {
            var builder = new ConfigurationBuilder()
                                //.SetBasePath(Directory.GetCurrentDirectory())
                                .AddIniFile(filename, false, true);
            var config = builder.Build();

            ConnectionType = String.IsNullOrEmpty(config["database:connection-type"]) ? "OleDB" : config["database:connection-type"];
            StringBuilder strb = new StringBuilder();

            if (ConnectionType == "ODBC")
            {
                strb.Append("DSN=" + config["database:dsn"] + ";");
                strb.Append("UID=" + config["database:username"] + ";");
                strb.Append("PWD=" + config["database:password"] + ";");
            }
            else if (ConnectionType == "OleDB")
            {
                
                strb.Append("server=" + config["database:server"]);
                strb.Append(String.IsNullOrEmpty(config["database:database"]) ? "" : ";database=" + config["database:database"]);
                strb.Append(String.IsNullOrEmpty(config["database:username"]) ? "" : ";user id=" + config["database:username"]);
                strb.Append(String.IsNullOrEmpty(config["database:password"]) ? "" : ";password=" + config["database:password"]);
            }
            else if (ConnectionType == "DBCFile")
            {
                var dbcm = new DbcFileManager(config["database:dbc-filename"]);
                strb.Append(dbcm.ConnectionString);
            }
            else if (ConnectionType == "String")
            {
                strb.Append(config["database:connection-string"]);
            }
            if (strb.Length != 0) ConnectionString = strb.ToString();

            if (!IsValid()) return;

        }
        public static bool IsValid()
        {
            return (ConnectionType != "") && (ConnectionString != "");
        }
    }
}
