import {
  addProviders,
  inject
} from '@angular/core/testing';

import {FormControlService} from "../../common/templates/form-control.service";
import {TestService} from "./test.service";
import {TestComponent} from "./test.component";
import {ActivatedRoute, Router, Params} from "@angular/router";
import {HTTP_PROVIDERS, BaseRequestOptions, Http, ResponseOptions, Response} from "@angular/http";
import {MockBackend} from "@angular/http/testing";
import {Test} from "./test";
import {Observable} from "rxjs/Rx";
import {DelegateService} from "../../common/services/delegate.service";
import {TestTemplate} from "./test-template";

describe('Test', () => {

  class MockActivatedRoute {
    params: Observable<Params>
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
    addProviders(
      [
        BaseRequestOptions,
        MockBackend,

        {
          provide: Http,
          useFactory: function(backend, defaultOptions) {
            return new Http(backend, defaultOptions);
          },
          deps: [MockBackend, BaseRequestOptions]
        },

        TestService,
        FormControlService,
        TestComponent,
        TestTemplate,
        DelegateService,

        {
          provide: ActivatedRoute,
          useClass: MockActivatedRoute
        }, {provide: Router, useClass: MockRouter}]
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
