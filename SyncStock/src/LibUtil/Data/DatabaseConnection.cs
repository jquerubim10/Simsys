using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace LibUtil.Data
{
    public class DatabaseConnection
    {
        #region Props
        public string Database { get; set; }
        public string Name { get; set; }
        public string Password { get; set; }
        public string Server { get; set; }
        public string User { get; set; }
        public bool IntegratedSecurity { get; set; }
        #endregion Props
        #region Ctor
        public DatabaseConnection() { }
        #endregion Ctor
    }
}
