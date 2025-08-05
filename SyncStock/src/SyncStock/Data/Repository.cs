using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;
using SyncStock.Domain;
using Dapper;
using Dommel;
using System.Data;

namespace SyncStock.Data
{
    public abstract class Repository<TEntity,TId> : IRepository<TEntity>
        where TEntity : EntityBase<TId>
    {
        private DbSession _session;
        public Repository(DbSession session)
        {
            _session = session;
        }

        public void Delete(object id)
        {
            var entity = GetById(id);
            if (entity == null) throw new Exception("Registro de id:" + id + ", não encontrado");
            Delete(entity);
        }
        public void Delete(IEnumerable<object> ids)
        {
            foreach(var id in ids)
            {
                Delete(id);
            }
        }
        public void Delete(TEntity entity)
        {
            if (!_session.Connection.Delete(entity,_session.Transaction))
                throw new ApplicationException("Não foi possível remover todas as entidades.");
        }
        public void Delete(IEnumerable<TEntity> entities)
        {
            for (int i = 0; i < entities.Count(); i++)
            {
                Delete(entities.ElementAt(i));
            }
        }
        public void Delete(Expression<Func<TEntity,bool>> filter = null)
        {
            Console.WriteLine("Excluido Movimento...");
            var reg_count = _session.Connection.Count(filter,_session.Transaction);
            var reg_del = _session.Connection.DeleteMultiple(filter,_session.Transaction);
            Console.WriteLine("Foram excluidos, " + reg_count + " Movimentações");
            if (reg_count != reg_del)
                throw new ApplicationException("Quantidade de registros excluidos incorreta.");
        }

        public List<TEntity> Get(Expression<Func<TEntity, bool>> filter = null, Func<IQueryable<TEntity>, IOrderedQueryable<TEntity>> orderBy = null)
        {
            return _session.Connection.Select<TEntity>(filter,_session.Transaction).ToList();
        }

        public List<TEntity> GetAll()
        {
            return _session.Connection.Select<TEntity>(null,_session.Transaction).ToList();
        }

        public TEntity GetById(object id)
        {
            return _session.Connection.Get<TEntity>(id, _session.Transaction);
        }

        public void Insert(TEntity entity)
        {            
            entity.SetNewIdentity(_session.Connection.Insert(entity,_session.Transaction));
        }
        //public void Insert(IEnumerable<TEntity> entities)
        //{
        //    _session.Connection.BulkInsert(entities);
        //}
        public void Insert(IEnumerable<TEntity> entities)
        {
            _session.Connection.InsertAll(entities,_session.Transaction);
        }
        //public void Insert(IEnumerable<TEntity> entities)
        //{
        //    for (int i = 0; i < entities.Count(); i++)
        //    {
        //        Insert(entities.ElementAt(i));
        //    }
        //}
        //public void Insert(IEnumerable<TEntity> entities)
        //{
        //    var insert = @"set nocount on;
        //                insert into [SE_HIST_MOVIMENTO_ESTOQUE] 
        //                ([ID_PROCESSAMENTO], [DATA_MOVIMENTO], [EVENTO], [MOVIMENTO], [DOCUMENTO], [SERIE], [DATA_EMISSAO], 
        //                 [PRODUTO], [LOTE], [VALIDADE], [QUANTIDADE], [CUSTO_UNIT], [CUSTO_TOTAL], [SALDO], [ID_ENTRADA], 
        //                 [ESTOQUECC], [CONSUMOCC], [SEQUENCIA], [TERCEIRO], [FORNECEDOR], [PEDIDO], [EMPENHOF], [DOACAO], 
        //                 [QUANTIDADE_FRA], [ID_ORIGEM_FRA], [SALDO_ALTERADO]) 
        //                values ";
        //    var values = @"(@ProcessingId, @EventDate, @EventType, @MovementType, @Document, @Series, @IssueDate, @ProductId, 
        //                    @LotNumber, @ValidityDate, @Quantity, @UnityValue, @TotalValue, @StockBalance, @EntryId, @StockId, 
        //                    @ConsumeId, @OriginalSequence, @OutSourceId, @ProviderId, @PurchaseOrder, @Pledge, @Donation, 
        //                    @FractionQuantity, @FractionSourceId, @NewStockBalance)";

        //    var cmd = insert;
        //    var args = new List<TEntity>();
        //    var insertedRows = 0;

        //    for (int i = 0; i < entities.Count(); i++)
        //    {
        //        if ((i + 1) % 100 == 0)
        //        {
        //            cmd = cmd.Trim(',') + ";";
        //            insertedRows += _session.Connection.Execute(cmd, args.ToArray(), _session.Transaction);
        //            cmd = insert;
        //            args.Clear();
        //        }
        //        args.Add(entities.ElementAt(i));
        //        cmd += values + ",";
        //    }

        //    insertedRows += _session.Connection.Execute(cmd, args, _session.Transaction);
        //    cmd = insert;
        //    args.Clear();
        //    if (insertedRows != entities.Count())
        //        throw new ApplicationException("Alguns registros não foram inseridos.");

        //}
        public void Update(TEntity entities)
        {
            _session.Connection.Update<TEntity>(entities, _session.Transaction);
        }
        public void Update(IEnumerable<TEntity> entities)
        {
            foreach (var entity in entities) {
                _session.Connection.Update(entity);
            }
        }
        public void InsertOrUpdate(TEntity entity)
        {
            if (!entity.IdEqualsTo(0))
            {
                Update(entity);
            }
            else
            {
                Insert(entity);
            }
        }
        public void InsertOrUpdate(IEnumerable<TEntity> entities)
        {
            return;
            //Insert(entities);
            //return;
            //for(int i = 0; i < entities.Count(); i++)
            //{
            //    //var x = entities.ElementAt(i);
            //    InsertOrUpdate(entities.ElementAt(i));
            //}
        }

    }
}
