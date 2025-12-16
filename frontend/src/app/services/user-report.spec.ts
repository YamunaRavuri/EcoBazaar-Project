import { TestBed } from '@angular/core/testing';

import { UserReportService } from './user-report';

describe('UserReportService', () => {
  let service: UserReportService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserReportService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});