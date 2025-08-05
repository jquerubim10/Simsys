using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SyncStock.Domain
{
    public class DailyBalance : EntityBase<long>
    {
        public DateTime BalanceDate { get; set; }
        public long EntryId { get; set; }
        public long StockId { get; set; }
        public long ProductId { get; set; }
        public string LotNumber { get; set; }
        public string ValidityDate { get; set; }
        public string Balance { get; set; }
        public double UnityValue { get; set; }
        public double TotalValue { get; set; }
        public override void SetNewIdentity(object id)
        {
            base.SetNewIdentity(long.Parse(id.ToString()));
        }

    }
}
