import {
  TestBed,
  inject
} from '@angular/core/testing';

import {FormControlService} from "../../common/templates/form-control.service";
import {TestService} from "./test.service";
import {TestComponent} from "./test.component";
import {ActivatedRoute, Router, Params} from "@angular/router";
import {BaseRequestOptions, Http, ResponseOptions, Response} from "@angular/http";
import {MockBackend} from "@angular/http/testing";
import {Test} from "./test";
import {Observable} from "rxjs/Rx";
import {DelegateService} from "../../common/services/delegate.service";
import {TestTemplate} from "./test-template";
import {BroadcastEmmitterService} from "../../common/services/broadcast-emitter.servie";
import {BroadcasterService} from "../../common/services/broadcaster.service";

describe('Test', () => {

  class MockActivatedRoute {
    params: Observable<Params>;

    constructor() {
      this.params = Observable.create(subscriber => {
        subscriber.next({id: 1});
        subscriber.complete();
      });
    }
  }

  class MockRouter {
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
        providers: [
          BaseRequestOptions,
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
          TestComponent,
          TestTemplate,
          DelegateService,
          BroadcastEmmitterService,
          BroadcasterService,
          {
            provide: ActivatedRoute,
            useClass: MockActivatedRoute
          }, {provide: Router, useClass: MockRouter}]
      }
    );
  });

  it('first test', inject([TestComponent, MockBackend], (test, mockBackend) => {
    let response = new Test();
    response.id = 1;

    let responseOptions = new ResponseOptions({body: response});
    mockBackend.connections.subscribe(
      c => c.mockRespond(new Response(responseOptions)));

    test.ngOnInit();
    expect(test.form).toBeDefined();
  }));
});
