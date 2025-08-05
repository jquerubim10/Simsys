using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SyncStock.Domain;

namespace SyncStock.Data
{
    public sealed class UnitOfWork : IUnitOfWork
    {
        private DbSession _session = null;
        internal UnitOfWork(DbSession session)
        {
            _session = session;
        }

        public void Begin()
        {
            _session.Transaction = _session.Connection.BeginTransaction();
        }

        public void Commit()
        {
            _session.Transaction.Commit();
            Dispose();
        }

        public void Rollback()
        {
            _session.Transaction.Rollback();
            Dispose();
        }

        public void Dispose() => _session.Transaction?.Dispose();

    }
}
