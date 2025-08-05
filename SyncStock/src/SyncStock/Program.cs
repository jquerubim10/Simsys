using System;
using System.IO;
using Microsoft.Extensions.Configuration;
using SyncStock.Data;
using System.Configuration;
using SyncStock.Domain;
using SyncStock.Service;

namespace SyncStock
{
    public class Program
    {

        public static int Main(string[] args)
        {
            //Read arguments

            //Read file configuration
            AppConfig.ReadConfigurationFile(@"SyncStock.config");
            Console.WriteLine("Iniciando execução...");
            try
            {
                //Process operations
                using (var session = new DbSession())
                {
                    var service = new ExecutionService(session);
                    service.SyncronizationProcess();
                }
            }catch(Exception ex)
            {
                Console.WriteLine("Deu errado!");
                Console.WriteLine(ex.Message);
            }
            Console.WriteLine("Sucesso!");
            return 1;
        }
    }
}
