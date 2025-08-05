using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dapper;

namespace SyncStock.Data
{
    public class UtilityRepository
    {
        private DbSession _session;
        public UtilityRepository(DbSession session)
        {
            _session = session;
        }
        public string GetUpstamp()
        {
            string date = GetDatetime().ToString("yyyy-MM-dd HH:mm:ss.fff");
            string version = "SS1.0.0";
            string username = "M2";
            return date + " " + version + " " + username;
        }
        public DateTime GetDatetime()
        {
            var sql = "SELECT GETDATE()";
            return _session.Connection.QuerySingle<DateTime>(sql, null, _session.Transaction);
        }
        public DateTime ClosedBalanceReferenceDate()
        {
            var sql = "SELECT ISNULL( MAX(DATA_FINAL) +1,'2022-01-01 00:00:00.000') FROM SE_FECHAMENTO WHERE ISNULL(CANCELADO,0) = 0";
            return _session.Connection.QuerySingle<DateTime>(sql, null, _session.Transaction);
        }
    }
}
