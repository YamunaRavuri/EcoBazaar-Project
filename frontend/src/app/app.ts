// import { Component, signal } from '@angular/core';
// import { RouterOutlet } from '@angular/router';
// import { NgxSpinnerModule } from 'ngx-spinner';

// import { Navbar } from './components/navbar/navbar';
// import { Footer } from './components/footer/footer';

// // import { Router } from '@angular/router';


// @Component({
//   selector: 'app-root',
//   standalone: true,
//   imports: [RouterOutlet, NgxSpinnerModule, Navbar, Footer],
//   templateUrl: './app.html',
//   styleUrl: './app.scss'
// })


// export class App {
//   protected readonly title = signal('ecobazaar-frontend');

// }


import { Component, signal } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';   // <-- ADD THIS
import { filter } from 'rxjs/operators';

import { NgxSpinnerModule } from 'ngx-spinner';
import { Navbar } from './components/navbar/navbar';
import { Footer } from './components/footer/footer';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NgxSpinnerModule,
    Navbar,
    Footer,
    CommonModule      // <-- ADD THIS
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {

  hideFooter = signal(false);

  constructor(public router: Router) {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        const url = event.url;
        this.hideFooter.set(
          url.startsWith('/login') ||
          url.startsWith('/register')
        );
      });
  }
}

