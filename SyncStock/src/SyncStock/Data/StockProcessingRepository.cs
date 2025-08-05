using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SyncStock.Domain;
using Dapper;
using System.Linq.Expressions;

namespace SyncStock.Data
{
    public class StockProcessingRepository : IRepository<StockProcessing>
    {
        private DbSession _session;

        public StockProcessingRepository(DbSession session)
        {
            _session = session;
        }

        public void Delete(object id)
        {
            var sql = "DELETE T FROM SE_HIST_MOVIMENTO_ESTOQUE T WHERE T.ID = @Id";
            _session.Connection.Execute(sql, new { Id = id }, _session.Transaction);
        }

        public void Delete(StockProcessing entity)
        {
            Delete(entity.Id);
        }

        public List<StockProcessing> Get(Expression<Func<StockProcessing, bool>> filter = null, Func<IQueryable<StockProcessing>, IOrderedQueryable<StockProcessing>> orderBy = null)
        {
            var query = GetAll().AsQueryable();

            if (filter != null)
            {
                query = query.Where(filter);
            }

            if (orderBy != null)
            {
                return orderBy(query).ToList();
            }
            else
            {
                return query.ToList();
            }

            throw new NotImplementedException();
        }

        public List<StockProcessing> GetAll()
        {
            var sql = "";
            sql = sql + "SELECT  ID AS Id " + "\n";
            sql = sql + "       ,DATA_INICIAL AS InitialTimestamp " + "\n";
            sql = sql + "       ,DATA_FINAL AS FinalTimestamp " + "\n";
            sql = sql + "       ,ATUALIZACAO AS Upstamp " + "\n";
            sql = sql + "FROM SE_HIST_PROCESSAMENTO_ESTOQUE";
            return _session.Connection.Query<StockProcessing>(sql, null, _session.Transaction).ToList();
        }

        public StockProcessing GetById(object id)
        {
            var sql = "";
            sql = sql + "SELECT  ID AS Id " + "\n";
            sql = sql + "       ,DATA_INICIAL AS InitialTimestamp " + "\n";
            sql = sql + "       ,DATA_FINAL AS FinalTimestamp " + "\n";
            sql = sql + "       ,ATUALIZACAO AS Upstamp " + "\n";
            sql = sql + "FROM SE_HIST_PROCESSAMENTO_ESTOQUE";
            sql = sql + "WHERE ID = @ID";
            return _session.Connection.Query<StockProcessing>(sql, new { ID = id}, _session.Transaction).FirstOrDefault();
           
        }

        public void Insert(ref StockProcessing entity)
        {
            var sql = @"INSERT INTO SE_HIST_PROCESSAMENTO_ESTOQUE(DATA_INICIAL, ATUALIZACAO) 
                        VALUES (@InitialTimestamp, @Upstamp);
                        SELECT CAST(SCOPE_IDENTITY() AS INT);";
            entity.Upstamp = _session.GetUtilityRepository().GetUpstamp();
            var args = new
            {
                InitialTimestamp = entity.InitialTimestamp,
                Upstamp = entity.Upstamp
            };
            entity.SetNewIdentity(_session.Connection.QuerySingle<long>(sql, args, _session.Transaction));
        }
        public void Update(StockProcessing entity)
        {
            var sql = "";
            sql = sql + "UPDATE SE_HIST_PROCESSAMENTO_ESTOQUE " + "\n";
            sql = sql + "SET DATA_FINAL = @FinalTimestamp " + "\n";
            sql = sql + "   ,ATUALIZACAO = @Upstamp " + "\n";
            sql = sql + "WHERE ID = @Id;";
            entity.Upstamp = _session.GetUtilityRepository().GetUpstamp();
            _session.Connection.Execute(sql, entity, _session.Transaction);
        }

        public void InsertOrUpdate(ref StockProcessing entity)
        {
            if(entity.Id != 0)
            {
                Update(entity);
            }
            else
            {
                Insert(ref entity);
            }
        }


    }
}
