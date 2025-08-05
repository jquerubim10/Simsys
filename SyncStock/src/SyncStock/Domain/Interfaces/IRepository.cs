using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace SyncStock.Domain
{
    public interface IRepository<TEntity>
    {
        TEntity GetById(object id);
        List<TEntity> GetAll();
        List<TEntity> Get(Expression<Func<TEntity, bool>> filter = null, Func<IQueryable<TEntity>, IOrderedQueryable<TEntity>> orderBy = null);
        void Insert(TEntity entity);
        void Insert(IEnumerable<TEntity> entities);
        void Update(TEntity entity);
        void Update(IEnumerable<TEntity> entities);
        void InsertOrUpdate(TEntity entity);
        void InsertOrUpdate(IEnumerable<TEntity> entities);
        void Delete(object id);
        void Delete(IEnumerable<object> ids);
        void Delete(TEntity entity);
        void Delete(IEnumerable<TEntity> entity);
        void Delete(Expression<Func<TEntity,bool>> filter = null);
    }
}
