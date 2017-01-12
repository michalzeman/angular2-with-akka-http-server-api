import {TestBed, inject} from "@angular/core/testing";
import {Observable} from "rxjs";
import {Params, ActivatedRoute, Router} from "@angular/router";
import {MockBackend} from "@angular/http/testing";
import {TestTableComponent} from "./test-table.component";
import {BaseRequestOptions, Http} from "@angular/http";
import {TestService} from "./test.service";
import {FormControlService} from "../../common/templates/form-control.service";
import {TestTemplate} from "./test-template";
import {DelegateService} from "../../common/services/delegate.service";
import {BroadcastEmmitterService} from "../../common/services/broadcast-emitter.servie";
import {BroadcasterService} from "../../common/services/broadcaster.service";


class MockRouter {
}
/**
 *basic providers for tests
 */
let basicProviders: any[] = [BaseRequestOptions,
  MockBackend,
  {
    provide: Http,
    useFactory: function (backend, defaultOptions) {
      return new Http(backend, defaultOptions);
    },
    deps: [MockBackend, BaseRequestOptions]
  },

  TestService,
  FormControlService,
  TestTableComponent,
  TestTemplate,
  DelegateService,
  BroadcastEmmitterService,
  BroadcasterService,
  {provide: Router, useClass: MockRouter}
];

describe("TestTableComponent test", () => {

  class MockActivatedRoute {
    queryParams: Observable<Params>;

    constructor() {
      this.queryParams = Observable.create(subscriber => {
        subscriber.next({page: "2"});
        subscriber.complete();
      });
    }

  }

  let providersOk: any[] = [{
    provide: ActivatedRoute,
    useClass: MockActivatedRoute
  }];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: providersOk.concat(basicProviders)
    }).compileComponents()
  });

  it('first query parameters OK', inject([TestTableComponent, MockBackend],
    (test, mockBackend) => {
      test.ngOnInit();
      expect(test.page).toEqual(2);
    }));

});

describe("TestTableComponent test NaN", () => {
  class MockActivatedRouteNaN {
    queryParams: Observable<Params>;

    constructor() {
      this.queryParams = Observable.create(subscriber => {
        subscriber.next({page: 'jskwejf'});
        subscriber.complete();
      });
    }
  }

  let providersNaN: any[] = [{
    provide: ActivatedRoute,
    useClass: MockActivatedRouteNaN
  }];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: providersNaN.concat(basicProviders)
    }).compileComponents()
  });

  it('first query parameters NaN', inject([TestTableComponent, MockBackend],
    (test, mockBackend) => {
      test.ngOnInit();
      expect(test.page).toEqual(1);
    }));

});


describe("TestTableComponent test query params negative", () => {
  class MockActivatedRouteNeg {
    queryParams: Observable<Params>;

    constructor() {
      this.queryParams = Observable.create(subscriber => {
        subscriber.next({page: -2});
        subscriber.complete();
      });
    }
  }

  let providersNaN: any[] = [{
    provide: ActivatedRoute,
    useClass: MockActivatedRouteNeg
  }];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: providersNaN.concat(basicProviders)
    }).compileComponents()
  });

  it('first query parameters NaN', inject([TestTableComponent, MockBackend],
    (test, mockBackend) => {
      test.ngOnInit();
      expect(test.page).toEqual(1);
    }));

});
