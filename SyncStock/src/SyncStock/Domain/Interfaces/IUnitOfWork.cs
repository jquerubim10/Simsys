using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SyncStock.Domain;

namespace SyncStock.Domain
{
    public interface IUnitOfWork : IDisposable
    {
        //IDictionary<TEntity,TRepository> Repository<TEntity,TRepository>();
        void Begin();
        void Commit();
        void Rollback();
    }
}
