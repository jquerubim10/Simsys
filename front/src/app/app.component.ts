import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HostListener } from '@angular/core';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    standalone: true,
    imports: [RouterOutlet]
})
export class AppComponent implements OnInit, OnDestroy {

    /**
     * Constructor
    */
    constructor() {
    }

    ngOnInit(): void {
        this.unloadHandler();
    }
    
    ngOnDestroy(): void {
        this.unloadHandler();
    }
    
    /**
     * 
     * @param event 
    */
    @HostListener('window:unload', ['$event'])
    unloadHandler(event?) {
        window.sessionStorage.clear();
        localStorage.clear();
    }
    
}
