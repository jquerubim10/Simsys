using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SyncStock.Data;
using SyncStock.Domain;
using LibUtil.Extension;

namespace SyncStock.Service
{
    public class ExecutionService
    {
        DbSession _session;
        public ExecutionService(DbSession session)
        {
            _session = session;
        }
        public void SyncronizationMovement(StockProcessing process, DateTime referenceDate, long product = 0)
        {

            //var hist_movims = _session.GetRepository<StockMovement>().Get(x => x.EventDate >= referenceDate);
            //_session.GetRepository<StockMovement>().Delete(hist_movims);
            _session.GetStockMovementRepository()
                    .Delete(x => x.EventDate >= referenceDate);
            _session.GetStockMovementRepository()
                    .SyncAllPending(process, referenceDate);
            //var new_movims = _session.GetStockMovementRepository()
            //                     .GetAllPending(process, referenceDate);
                                 
            //Get a list of StockMovement where hasn't be syncronized
            //_session.GetStockMovementRepository().Insert(new_movims);
            

            //Get first datetime movement of range to be reference

            //
        }
        public void SyncronizationBalance(StockProcessing process, DateTime referenceDate, long product=0)
        {
            _session.GetDailyBalanceRepository()
                .Process(process, referenceDate);
        }
        public void SyncronizationProcess()
        {
            try
            {
                _session.UnitOfWork.Begin();

               var referenceDate = _session.GetUtilityRepository()
                                           .ClosedBalanceReferenceDate();

               var process = new StockProcessing() {
                    InitialTimestamp = _session.GetUtilityRepository()
                                               .GetDatetime(),
                    Upstamp = _session.GetUtilityRepository()
                                      .GetUpstamp()
                };
                _session.GetRepository<StockProcessing>().Insert(process);
                SyncronizationMovement(process, referenceDate);
                SyncronizationBalance(process, referenceDate);
                process.FinalTimestamp = _session.GetUtilityRepository()
                                                 .GetDatetime();
                process.Upstamp = _session.GetUtilityRepository()
                                          .GetUpstamp();
                _session.UnitOfWork.Commit();
            }catch(Exception)
            {
                throw;
            }
        }
    }
}
