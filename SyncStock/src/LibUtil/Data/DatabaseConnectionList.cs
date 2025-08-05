using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace LibUtil.Data
{
    public class DatabaseConnectionList
    {
        public List<DatabaseConnection> Items { get; set; }
        public DatabaseConnectionList()
        {
            Items = new List<DatabaseConnection>();
        }
        public void AddItem(DatabaseConnection conn)
        {
            Items.Add(conn);
        }
    }
}
