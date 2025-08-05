using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SyncStock.Domain
{
    public abstract class EntityBase<TId>
    {
        public TId Id { get; protected set; }

        public virtual void SetNewIdentity(object id)
        {
            Id = (TId) id;
        }
        public bool IdEqualsTo(object value)
        {
            return (Id.ToString() == value.ToString());
        }
    }
}
