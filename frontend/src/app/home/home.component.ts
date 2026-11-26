import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  message = 'Loading...';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get<{ message: string }>('/api/hello')
      .subscribe(r => this.message = r.message);
  }
}
