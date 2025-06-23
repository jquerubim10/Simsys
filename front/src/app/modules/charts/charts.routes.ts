import { Routes } from "@angular/router";
import { ChatMonitoramentoComponent } from "./chat-monitoramento/chat-monitoramento.component";
import { ChartSignalVitalComponent } from "./chart-signal-vital/chart-signal-vital.component";

export default [
    {
        path     : '',
        children: [
            {
                path: 'monitor',
                component: ChatMonitoramentoComponent
            },
            {
                path: 'signal',
                component: ChartSignalVitalComponent
            },
        ]
    },
] as Routes;