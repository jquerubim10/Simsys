using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dapper.FluentMap;
using Dapper.FluentMap.Dommel;
using LibUtil.Data;
using SyncStock.Domain;

namespace SyncStock.Data
{
    public sealed class DbSession : IDisposable
    {
        public IDbConnection Connection { get; }
        public IDbTransaction Transaction { get; set; }
        public IUnitOfWork UnitOfWork { get; private set; }

        private IDictionary<string, object> _repos;
        public DbSession()
        {   
            Connection = ConnectionManager.GetConnection(AppConfig.ConnectionString);
            Connection.Open();
            RegisterMapping();
            UnitOfWork = new UnitOfWork(this);
            _repos = new Dictionary<string, object>();
            _repos.Add(typeof(StockMovement).Name, new StockMovementRepository(this));
            _repos.Add(typeof(StockProcessing).Name, new StockProcessing2Repository(this));
            _repos.Add(typeof(DailyBalance).Name, new DailyBalanceRepository(this));
            _repos.Add(typeof(UtilityRepository).Name, new UtilityRepository(this));            

        }
        public IRepository<TEntity> GetRepository<TEntity>() 
        {
            return (IRepository< TEntity >)  _repos[typeof(TEntity).Name];
        }
        public StockMovementRepository GetStockMovementRepository()
        {
            return (StockMovementRepository)_repos[typeof(StockMovement).Name];
        }
        public DailyBalanceRepository GetDailyBalanceRepository()
        {
            return (DailyBalanceRepository)_repos[typeof(DailyBalance).Name];
        }
        public UtilityRepository GetUtilityRepository()
        {
            return (UtilityRepository) _repos[typeof(UtilityRepository).Name];
        }
        public void Dispose() => Connection?.Dispose();

        private static void RegisterMapping()
        {
            FluentMapper.Initialize(config => {
                config.AddMap(new StockProcessingMap());
                config.AddMap(new DailyBalanceMap());
                config.AddMap(new StockMovementMap());
                config.ForDommel();
            });
        }
    }
}
