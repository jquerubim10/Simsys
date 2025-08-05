using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SyncStock.Domain
{
    public class StockProcessing : EntityBase<long>
    {
        public DateTime InitialTimestamp { get; set; }
        public DateTime? FinalTimestamp { get; set; }
        public String Upstamp { get; set; }
        public override void SetNewIdentity(object id)
        {
            base.SetNewIdentity(long.Parse(id.ToString()));
        }
    }
}
