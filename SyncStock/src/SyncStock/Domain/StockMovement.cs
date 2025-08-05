using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SyncStock.Domain
{
    public class StockMovement : EntityBase<long>
    {
        public long ProcessingId { get; set; }
        public StockProcessing? Processing { get; set; }
        public DateTime EventDate { get; set; }
        private EStockEventType _eventType;
        public string EventType { get { return _eventType.ToString(); } set { _eventType = Enum.Parse<EStockEventType>(value); }}
        public EStockMovementType MovementType { get; set; }
        public string Document { get; set; }
        public string Series { get; set; }
        public string IssueDate { get; set; }
        public long ProductId { get; set; }
        public string LotNumber { get; set; }
        public DateTime ValidityDate { get; set; }
        public double Quantity { get; set; }
        public double UnityValue { get; set; }
        public double TotalValue { get; set; }
        public double StockBalance { get; set; }
        public long EntryId { get; set; }
        public long StockId {get;set;}
        public long? ConsumeId { get; set; }
        public long? OriginalSequence { get; set; }
        public long? OutSourceId { get; set; }
        public long? ProviderId { get; set;}
        public string? PurchaseOrder { get; set; }
        public string Pledge { get; set; }
        public bool Donation { get; set; }
        public double? FractionQuantity { get; set; }
        public long? FractionSourceId { get; set; }
        public long? NewStockBalance { get; set; }

        public string GetSignature()
        {
            StringBuilder sb = new StringBuilder();

            //var hash = "";
            return sb.ToString();
        }
        public override void SetNewIdentity(object id)
        {
            base.SetNewIdentity(long.Parse(id.ToString()));
        }
    }
}
