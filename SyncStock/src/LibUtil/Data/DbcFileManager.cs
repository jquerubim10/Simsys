using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Serialization;
using LibUtil.Security;

namespace LibUtil.Data
{
    public class DbcFileManager
    {
        public string ConnectionString { get; private set; }
        public DbcFileManager(string dbcFilePath){

            string pathFileConnection = dbcFilePath;
            try
            {
                string XmlContent;
                DatabaseConnectionList dbs;
                DatabaseConnection dbc = null;

                if (File.Exists(pathFileConnection))
                {
                    XmlContent = AESCryptography.AESDecryptFileToText(pathFileConnection, LibUtilConfig.KeyPrivate);
                    XmlSerializer serializer = new XmlSerializer(typeof(DatabaseConnectionList));
                    dbs = (DatabaseConnectionList)serializer.Deserialize(new StringReader(XmlContent));

                    foreach (DatabaseConnection conn in dbs.Items)
                    {
                        dbc = conn;
                    }
                    if (dbc != null)
                    {
                        var strBuilder = new SqlConnectionStringBuilder();

                        strBuilder.DataSource = dbc.Server;
                        strBuilder.IntegratedSecurity = dbc.IntegratedSecurity;
                        if (!dbc.IntegratedSecurity)
                        {
                            strBuilder.UserID = (dbc.User == null ? String.Empty : dbc.User);
                            strBuilder.Password = (dbc.Password == null ? String.Empty : dbc.Password);
                        }

                        strBuilder.InitialCatalog = (dbc.Database == null ? String.Empty : dbc.Database);

                        //Permite executar multiplos DataReaders em uma mesma conexão.
                        strBuilder.MultipleActiveResultSets = true;

                        ConnectionString = strBuilder.ConnectionString;
                    }
                    else
                    {
                        throw new Exception("Conexão não identificada no arquivo DBC.");
                    }
                }
                else
                {
                    throw new Exception("Caminho do arquivo DBC está incorreto.");
                }

            }
            catch (Exception ex)
            {
                throw ex;
            }
        }

    }
}
