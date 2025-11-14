# k6 Test Results

This file contains the latest results from k6 performance tests. Results are automatically updated after each test run.

**Related Documentation:**
- **[README](../README.md)** - Project overview and quick start
- **[K6 Performance Testing](K6_PERFORMANCE.md)** - Comprehensive k6 testing guide
- **[Development Guide](DEVELOPMENT.md)** - Complete development guide with all make commands

## Warm-up Test - 2025-11-14 13:36:08


         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/warmup-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 2 max VUs, 40s max duration (incl. graceful stop):
              * default: Up to 2 looping VUs for 10s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (01.0s), 1/2 VUs, 44 complete and 0 interrupted iterations
default   [  10% ] 1/2 VUs  01.0s/10.0s

running (02.0s), 1/2 VUs, 165 complete and 0 interrupted iterations
default   [  20% ] 1/2 VUs  02.0s/10.0s

running (03.0s), 1/2 VUs, 317 complete and 0 interrupted iterations
default   [  30% ] 1/2 VUs  03.0s/10.0s

running (04.0s), 1/2 VUs, 479 complete and 0 interrupted iterations
default   [  40% ] 1/2 VUs  04.0s/10.0s

running (05.0s), 1/2 VUs, 648 complete and 0 interrupted iterations
default   [  50% ] 1/2 VUs  05.0s/10.0s

running (06.0s), 2/2 VUs, 999 complete and 0 interrupted iterations
default   [  60% ] 2/2 VUs  06.0s/10.0s

running (07.0s), 2/2 VUs, 1419 complete and 0 interrupted iterations
default   [  70% ] 2/2 VUs  07.0s/10.0s

running (08.0s), 2/2 VUs, 1861 complete and 0 interrupted iterations
default   [  80% ] 2/2 VUs  08.0s/10.0s

running (09.0s), 2/2 VUs, 2319 complete and 0 interrupted iterations
default   [  90% ] 2/2 VUs  09.0s/10.0s

running (10.0s), 2/2 VUs, 2793 complete and 0 interrupted iterations
default   [ 100% ] 2/2 VUs  10.0s/10.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<500' p(95)=7.46ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 16782   1677.983372/s
    checks_succeeded...................: 100.00% 16782 out of 16782
    checks_failed......................: 0.00%   0 out of 16782

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ warmup: response time < 500ms
    ✓ warmup: response has body

    HTTP
    http_req_duration.......................................................: avg=4.97ms min=2.63ms med=4.26ms max=290.29ms p(90)=6.37ms p(95)=7.46ms
      { expected_response:true }............................................: avg=4.97ms min=2.63ms med=4.26ms max=290.29ms p(90)=6.37ms p(95)=7.46ms
    http_req_failed.........................................................: 0.00%  0 out of 2797
    http_reqs...............................................................: 2797   279.663895/s

    EXECUTION
    iteration_duration......................................................: avg=5.34ms min=2.88ms med=4.54ms max=309.58ms p(90)=7.02ms p(95)=8.08ms
    iterations..............................................................: 2797   279.663895/s
    vus.....................................................................: 2      min=1         max=2
    vus_max.................................................................: 2      min=2         max=2

    NETWORK
    data_received...........................................................: 749 kB 75 kB/s
    data_sent...............................................................: 1.1 MB 113 kB/s




running (10.0s), 0/2 VUs, 2797 complete and 0 interrupted iterations
default ✓ [ 100% ] 0/2 VUs  10s


## Smoke Test - 2025-11-14 13:37:12


         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/smoke-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 10 max VUs, 1m30s max duration (incl. graceful stop):
              * default: Up to 10 looping VUs for 1m0s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (0m01.0s), 01/10 VUs, 251 complete and 0 interrupted iterations
default   [   2% ] 01/10 VUs  0m01.0s/1m00.0s

running (0m02.0s), 01/10 VUs, 457 complete and 0 interrupted iterations
default   [   3% ] 01/10 VUs  0m02.0s/1m00.0s

running (0m03.0s), 01/10 VUs, 723 complete and 0 interrupted iterations
default   [   5% ] 01/10 VUs  0m03.0s/1m00.0s

running (0m04.0s), 02/10 VUs, 1156 complete and 0 interrupted iterations
default   [   7% ] 02/10 VUs  0m04.0s/1m00.0s

running (0m05.0s), 02/10 VUs, 1670 complete and 0 interrupted iterations
default   [   8% ] 02/10 VUs  0m05.0s/1m00.0s

running (0m06.0s), 02/10 VUs, 2221 complete and 0 interrupted iterations
default   [  10% ] 02/10 VUs  0m06.0s/1m00.0s

running (0m07.0s), 03/10 VUs, 2793 complete and 0 interrupted iterations
default   [  12% ] 03/10 VUs  0m07.0s/1m00.0s

running (0m08.0s), 03/10 VUs, 3451 complete and 0 interrupted iterations
default   [  13% ] 03/10 VUs  0m08.0s/1m00.0s

running (0m09.0s), 03/10 VUs, 4286 complete and 0 interrupted iterations
default   [  15% ] 03/10 VUs  0m09.0s/1m00.0s

running (0m10.0s), 03/10 VUs, 5131 complete and 0 interrupted iterations
default   [  17% ] 03/10 VUs  0m10.0s/1m00.0s

running (0m11.0s), 04/10 VUs, 6229 complete and 0 interrupted iterations
default   [  18% ] 04/10 VUs  0m11.0s/1m00.0s

running (0m12.0s), 04/10 VUs, 7369 complete and 0 interrupted iterations
default   [  20% ] 04/10 VUs  0m12.0s/1m00.0s

running (0m13.0s), 04/10 VUs, 8294 complete and 0 interrupted iterations
default   [  22% ] 04/10 VUs  0m13.0s/1m00.0s

running (0m14.0s), 05/10 VUs, 9552 complete and 0 interrupted iterations
default   [  23% ] 05/10 VUs  0m14.0s/1m00.0s

running (0m15.0s), 05/10 VUs, 10724 complete and 0 interrupted iterations
default   [  25% ] 05/10 VUs  0m15.0s/1m00.0s

running (0m16.0s), 05/10 VUs, 12299 complete and 0 interrupted iterations
default   [  27% ] 05/10 VUs  0m16.0s/1m00.0s

running (0m17.0s), 06/10 VUs, 13577 complete and 0 interrupted iterations
default   [  28% ] 06/10 VUs  0m17.0s/1m00.0s

running (0m18.0s), 06/10 VUs, 15237 complete and 0 interrupted iterations
default   [  30% ] 06/10 VUs  0m18.0s/1m00.0s

running (0m19.0s), 06/10 VUs, 17003 complete and 0 interrupted iterations
default   [  32% ] 06/10 VUs  0m19.0s/1m00.0s

running (0m20.0s), 07/10 VUs, 19087 complete and 0 interrupted iterations
default   [  33% ] 07/10 VUs  0m20.0s/1m00.0s

running (0m21.0s), 07/10 VUs, 21154 complete and 0 interrupted iterations
default   [  35% ] 07/10 VUs  0m21.0s/1m00.0s

running (0m22.0s), 07/10 VUs, 23136 complete and 0 interrupted iterations
default   [  37% ] 07/10 VUs  0m22.0s/1m00.0s

running (0m23.0s), 07/10 VUs, 25328 complete and 0 interrupted iterations
default   [  38% ] 07/10 VUs  0m23.0s/1m00.0s

running (0m24.0s), 08/10 VUs, 27487 complete and 0 interrupted iterations
default   [  40% ] 08/10 VUs  0m24.0s/1m00.0s

running (0m25.0s), 08/10 VUs, 30284 complete and 0 interrupted iterations
default   [  42% ] 08/10 VUs  0m25.0s/1m00.0s

running (0m26.0s), 08/10 VUs, 32824 complete and 0 interrupted iterations
default   [  43% ] 08/10 VUs  0m26.0s/1m00.0s

running (0m27.0s), 09/10 VUs, 35366 complete and 0 interrupted iterations
default   [  45% ] 09/10 VUs  0m27.0s/1m00.0s

running (0m28.0s), 09/10 VUs, 38080 complete and 0 interrupted iterations
default   [  47% ] 09/10 VUs  0m28.0s/1m00.0s

running (0m29.0s), 09/10 VUs, 41043 complete and 0 interrupted iterations
default   [  48% ] 09/10 VUs  0m29.0s/1m00.0s

running (0m30.0s), 09/10 VUs, 43963 complete and 0 interrupted iterations
default   [  50% ] 09/10 VUs  0m30.0s/1m00.0s

running (0m31.0s), 10/10 VUs, 47141 complete and 0 interrupted iterations
default   [  52% ] 10/10 VUs  0m31.0s/1m00.0s

running (0m32.0s), 10/10 VUs, 49608 complete and 0 interrupted iterations
default   [  53% ] 10/10 VUs  0m32.0s/1m00.0s

running (0m33.0s), 10/10 VUs, 52181 complete and 0 interrupted iterations
default   [  55% ] 10/10 VUs  0m33.0s/1m00.0s

running (0m34.0s), 10/10 VUs, 55242 complete and 0 interrupted iterations
default   [  57% ] 10/10 VUs  0m34.0s/1m00.0s

running (0m35.0s), 10/10 VUs, 58349 complete and 0 interrupted iterations
default   [  58% ] 10/10 VUs  0m35.0s/1m00.0s

running (0m36.0s), 10/10 VUs, 60917 complete and 0 interrupted iterations
default   [  60% ] 10/10 VUs  0m36.0s/1m00.0s

running (0m37.0s), 10/10 VUs, 63339 complete and 0 interrupted iterations
default   [  62% ] 10/10 VUs  0m37.0s/1m00.0s

running (0m38.0s), 10/10 VUs, 66064 complete and 0 interrupted iterations
default   [  63% ] 10/10 VUs  0m38.0s/1m00.0s

running (0m39.0s), 10/10 VUs, 68372 complete and 0 interrupted iterations
default   [  65% ] 10/10 VUs  0m39.0s/1m00.0s

running (0m40.0s), 10/10 VUs, 70675 complete and 0 interrupted iterations
default   [  67% ] 10/10 VUs  0m40.0s/1m00.0s

running (0m41.0s), 10/10 VUs, 73898 complete and 0 interrupted iterations
default   [  68% ] 10/10 VUs  0m41.0s/1m00.0s

running (0m42.0s), 10/10 VUs, 77035 complete and 0 interrupted iterations
default   [  70% ] 10/10 VUs  0m42.0s/1m00.0s

running (0m43.0s), 10/10 VUs, 80264 complete and 0 interrupted iterations
default   [  72% ] 10/10 VUs  0m43.0s/1m00.0s

running (0m44.0s), 10/10 VUs, 83530 complete and 0 interrupted iterations
default   [  73% ] 10/10 VUs  0m44.0s/1m00.0s

running (0m45.0s), 10/10 VUs, 86510 complete and 0 interrupted iterations
default   [  75% ] 10/10 VUs  0m45.0s/1m00.0s

running (0m46.0s), 10/10 VUs, 89562 complete and 0 interrupted iterations
default   [  77% ] 10/10 VUs  0m46.0s/1m00.0s

running (0m47.0s), 10/10 VUs, 92743 complete and 0 interrupted iterations
default   [  78% ] 10/10 VUs  0m47.0s/1m00.0s

running (0m48.0s), 10/10 VUs, 95599 complete and 0 interrupted iterations
default   [  80% ] 10/10 VUs  0m48.0s/1m00.0s

running (0m49.0s), 10/10 VUs, 98958 complete and 0 interrupted iterations
default   [  82% ] 10/10 VUs  0m49.0s/1m00.0s

running (0m50.0s), 10/10 VUs, 102311 complete and 0 interrupted iterations
default   [  83% ] 10/10 VUs  0m50.0s/1m00.0s

running (0m51.0s), 10/10 VUs, 105472 complete and 0 interrupted iterations
default   [  85% ] 10/10 VUs  0m51.0s/1m00.0s

running (0m52.0s), 10/10 VUs, 108760 complete and 0 interrupted iterations
default   [  87% ] 10/10 VUs  0m52.0s/1m00.0s

running (0m53.0s), 10/10 VUs, 112042 complete and 0 interrupted iterations
default   [  88% ] 10/10 VUs  0m53.0s/1m00.0s

running (0m54.0s), 10/10 VUs, 115249 complete and 0 interrupted iterations
default   [  90% ] 10/10 VUs  0m54.0s/1m00.0s

running (0m55.0s), 10/10 VUs, 118124 complete and 0 interrupted iterations
default   [  92% ] 10/10 VUs  0m55.0s/1m00.0s

running (0m56.0s), 10/10 VUs, 121276 complete and 0 interrupted iterations
default   [  93% ] 10/10 VUs  0m56.0s/1m00.0s

running (0m57.0s), 10/10 VUs, 124338 complete and 0 interrupted iterations
default   [  95% ] 10/10 VUs  0m57.0s/1m00.0s

running (0m58.0s), 10/10 VUs, 127395 complete and 0 interrupted iterations
default   [  97% ] 10/10 VUs  0m58.0s/1m00.0s

running (0m59.0s), 10/10 VUs, 130709 complete and 0 interrupted iterations
default   [  98% ] 10/10 VUs  0m59.0s/1m00.0s

running (1m00.0s), 10/10 VUs, 133902 complete and 0 interrupted iterations
default   [ 100% ] 10/10 VUs  1m00.0s/1m00.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<200' p(95)=5.32ms
    ✓ 'p(99)<500' p(99)=13.11ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 803472  13387.152394/s
    checks_succeeded...................: 100.00% 803472 out of 803472
    checks_failed......................: 0.00%   0 out of 803472

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ smoke: response time < 200ms
    ✓ smoke: response has body

    HTTP
    http_req_duration.......................................................: avg=3.21ms min=1.11ms med=2.73ms max=144.92ms p(90)=4.18ms p(95)=5.32ms
      { expected_response:true }............................................: avg=3.21ms min=1.11ms med=2.73ms max=144.92ms p(90)=4.18ms p(95)=5.32ms
    http_req_failed.........................................................: 0.00%  0 out of 133912
    http_reqs...............................................................: 133912 2231.192066/s

    EXECUTION
    iteration_duration......................................................: avg=3.35ms min=1.2ms  med=2.85ms max=145.13ms p(90)=4.38ms p(95)=5.56ms
    iterations..............................................................: 133912 2231.192066/s
    vus.....................................................................: 10     min=1           max=10
    vus_max.................................................................: 10     min=10          max=10

    NETWORK
    data_received...........................................................: 36 MB  598 kB/s
    data_sent...............................................................: 54 MB  899 kB/s




running (1m00.0s), 00/10 VUs, 133912 complete and 0 interrupted iterations
default ✓ [ 100% ] 00/10 VUs  1m0s


## Load Test - 2025-11-14 13:39:17


         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/load-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 350 max VUs, 2m30s max duration (incl. graceful stop):
              * default: Up to 350 looping VUs for 2m0s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (0m01.0s), 012/350 VUs, 1812 complete and 0 interrupted iterations
default   [   1% ] 012/350 VUs  0m01.0s/2m00.0s

running (0m02.0s), 023/350 VUs, 4168 complete and 0 interrupted iterations
default   [   2% ] 023/350 VUs  0m02.0s/2m00.0s

running (0m03.0s), 035/350 VUs, 6031 complete and 0 interrupted iterations
default   [   2% ] 035/350 VUs  0m03.0s/2m00.0s

running (0m04.0s), 047/350 VUs, 8282 complete and 0 interrupted iterations
default   [   3% ] 047/350 VUs  0m04.0s/2m00.0s

running (0m05.0s), 058/350 VUs, 11669 complete and 0 interrupted iterations
default   [   4% ] 058/350 VUs  0m05.0s/2m00.0s

running (0m06.0s), 070/350 VUs, 15645 complete and 0 interrupted iterations
default   [   5% ] 070/350 VUs  0m06.0s/2m00.0s

running (0m07.0s), 082/350 VUs, 19882 complete and 0 interrupted iterations
default   [   6% ] 082/350 VUs  0m07.0s/2m00.0s

running (0m08.0s), 093/350 VUs, 24369 complete and 0 interrupted iterations
default   [   7% ] 093/350 VUs  0m08.0s/2m00.0s

running (0m09.0s), 105/350 VUs, 28787 complete and 0 interrupted iterations
default   [   7% ] 105/350 VUs  0m09.0s/2m00.0s

running (0m10.0s), 116/350 VUs, 33186 complete and 0 interrupted iterations
default   [   8% ] 116/350 VUs  0m10.0s/2m00.0s

running (0m11.0s), 128/350 VUs, 38039 complete and 0 interrupted iterations
default   [   9% ] 128/350 VUs  0m11.0s/2m00.0s

running (0m12.0s), 140/350 VUs, 42627 complete and 0 interrupted iterations
default   [  10% ] 140/350 VUs  0m12.0s/2m00.0s

running (0m13.0s), 151/350 VUs, 47602 complete and 0 interrupted iterations
default   [  11% ] 151/350 VUs  0m13.0s/2m00.0s

running (0m14.0s), 163/350 VUs, 52447 complete and 0 interrupted iterations
default   [  12% ] 163/350 VUs  0m14.0s/2m00.0s

running (0m15.0s), 175/350 VUs, 57789 complete and 0 interrupted iterations
default   [  12% ] 175/350 VUs  0m15.0s/2m00.0s

running (0m16.0s), 186/350 VUs, 62892 complete and 0 interrupted iterations
default   [  13% ] 186/350 VUs  0m16.0s/2m00.0s

running (0m17.0s), 198/350 VUs, 68241 complete and 0 interrupted iterations
default   [  14% ] 198/350 VUs  0m17.0s/2m00.0s

running (0m18.0s), 210/350 VUs, 73472 complete and 0 interrupted iterations
default   [  15% ] 210/350 VUs  0m18.0s/2m00.0s

running (0m19.0s), 221/350 VUs, 78382 complete and 0 interrupted iterations
default   [  16% ] 221/350 VUs  0m19.0s/2m00.0s

running (0m20.0s), 233/350 VUs, 82982 complete and 0 interrupted iterations
default   [  17% ] 233/350 VUs  0m20.0s/2m00.0s

running (0m21.0s), 244/350 VUs, 87728 complete and 0 interrupted iterations
default   [  17% ] 244/350 VUs  0m21.0s/2m00.0s

running (0m22.0s), 256/350 VUs, 92689 complete and 0 interrupted iterations
default   [  18% ] 256/350 VUs  0m22.0s/2m00.0s

running (0m23.0s), 268/350 VUs, 97277 complete and 0 interrupted iterations
default   [  19% ] 268/350 VUs  0m23.0s/2m00.0s

running (0m24.0s), 279/350 VUs, 101339 complete and 0 interrupted iterations
default   [  20% ] 279/350 VUs  0m24.0s/2m00.0s

running (0m25.0s), 291/350 VUs, 106761 complete and 0 interrupted iterations
default   [  21% ] 291/350 VUs  0m25.0s/2m00.0s

running (0m26.0s), 303/350 VUs, 111958 complete and 0 interrupted iterations
default   [  22% ] 303/350 VUs  0m26.0s/2m00.0s

running (0m27.0s), 314/350 VUs, 117625 complete and 0 interrupted iterations
default   [  22% ] 314/350 VUs  0m27.0s/2m00.0s

running (0m28.0s), 326/350 VUs, 121690 complete and 0 interrupted iterations
default   [  23% ] 326/350 VUs  0m28.0s/2m00.0s

running (0m29.0s), 338/350 VUs, 127417 complete and 0 interrupted iterations
default   [  24% ] 338/350 VUs  0m29.0s/2m00.0s

running (0m30.0s), 349/350 VUs, 133181 complete and 0 interrupted iterations
default   [  25% ] 349/350 VUs  0m30.0s/2m00.0s

running (0m31.0s), 350/350 VUs, 137437 complete and 0 interrupted iterations
default   [  26% ] 350/350 VUs  0m31.0s/2m00.0s

running (0m32.0s), 350/350 VUs, 141007 complete and 0 interrupted iterations
default   [  27% ] 350/350 VUs  0m32.0s/2m00.0s

running (0m33.0s), 350/350 VUs, 145326 complete and 0 interrupted iterations
default   [  27% ] 350/350 VUs  0m33.0s/2m00.0s

running (0m34.0s), 350/350 VUs, 150412 complete and 0 interrupted iterations
default   [  28% ] 350/350 VUs  0m34.0s/2m00.0s

running (0m35.0s), 350/350 VUs, 154946 complete and 0 interrupted iterations
default   [  29% ] 350/350 VUs  0m35.0s/2m00.0s

running (0m36.0s), 350/350 VUs, 159035 complete and 0 interrupted iterations
default   [  30% ] 350/350 VUs  0m36.0s/2m00.0s

running (0m37.0s), 350/350 VUs, 165098 complete and 0 interrupted iterations
default   [  31% ] 350/350 VUs  0m37.0s/2m00.0s

running (0m38.0s), 350/350 VUs, 170813 complete and 0 interrupted iterations
default   [  32% ] 350/350 VUs  0m38.0s/2m00.0s

running (0m39.0s), 350/350 VUs, 176033 complete and 0 interrupted iterations
default   [  32% ] 350/350 VUs  0m39.0s/2m00.0s

running (0m40.0s), 350/350 VUs, 181712 complete and 0 interrupted iterations
default   [  33% ] 350/350 VUs  0m40.0s/2m00.0s

running (0m41.0s), 350/350 VUs, 187117 complete and 0 interrupted iterations
default   [  34% ] 350/350 VUs  0m41.0s/2m00.0s

running (0m42.0s), 350/350 VUs, 192473 complete and 0 interrupted iterations
default   [  35% ] 350/350 VUs  0m42.0s/2m00.0s

running (0m43.0s), 350/350 VUs, 196994 complete and 0 interrupted iterations
default   [  36% ] 350/350 VUs  0m43.0s/2m00.0s

running (0m44.0s), 350/350 VUs, 201664 complete and 0 interrupted iterations
default   [  37% ] 350/350 VUs  0m44.0s/2m00.0s

running (0m45.0s), 350/350 VUs, 207514 complete and 0 interrupted iterations
default   [  37% ] 350/350 VUs  0m45.0s/2m00.0s

running (0m46.0s), 350/350 VUs, 213195 complete and 0 interrupted iterations
default   [  38% ] 350/350 VUs  0m46.0s/2m00.0s

running (0m47.0s), 350/350 VUs, 219189 complete and 0 interrupted iterations
default   [  39% ] 350/350 VUs  0m47.0s/2m00.0s

running (0m48.0s), 350/350 VUs, 223668 complete and 0 interrupted iterations
default   [  40% ] 350/350 VUs  0m48.0s/2m00.0s

running (0m49.0s), 350/350 VUs, 229724 complete and 0 interrupted iterations
default   [  41% ] 350/350 VUs  0m49.0s/2m00.0s

running (0m50.0s), 350/350 VUs, 234458 complete and 0 interrupted iterations
default   [  42% ] 350/350 VUs  0m50.0s/2m00.0s

running (0m51.0s), 350/350 VUs, 239603 complete and 0 interrupted iterations
default   [  42% ] 350/350 VUs  0m51.0s/2m00.0s

running (0m52.0s), 350/350 VUs, 244732 complete and 0 interrupted iterations
default   [  43% ] 350/350 VUs  0m52.0s/2m00.0s

running (0m53.0s), 350/350 VUs, 249346 complete and 0 interrupted iterations
default   [  44% ] 350/350 VUs  0m53.0s/2m00.0s

running (0m54.0s), 350/350 VUs, 254791 complete and 0 interrupted iterations
default   [  45% ] 350/350 VUs  0m54.0s/2m00.0s

running (0m55.0s), 350/350 VUs, 260555 complete and 0 interrupted iterations
default   [  46% ] 350/350 VUs  0m55.0s/2m00.0s

running (0m56.0s), 350/350 VUs, 265231 complete and 0 interrupted iterations
default   [  47% ] 350/350 VUs  0m56.0s/2m00.0s

running (0m57.0s), 350/350 VUs, 271074 complete and 0 interrupted iterations
default   [  47% ] 350/350 VUs  0m57.0s/2m00.0s

running (0m58.0s), 350/350 VUs, 275010 complete and 0 interrupted iterations
default   [  48% ] 350/350 VUs  0m58.0s/2m00.0s

running (0m59.0s), 350/350 VUs, 277411 complete and 0 interrupted iterations
default   [  49% ] 350/350 VUs  0m59.0s/2m00.0s

running (1m00.0s), 350/350 VUs, 281097 complete and 0 interrupted iterations
default   [  50% ] 350/350 VUs  1m00.0s/2m00.0s

running (1m01.0s), 350/350 VUs, 283949 complete and 0 interrupted iterations
default   [  51% ] 350/350 VUs  1m01.0s/2m00.0s

running (1m02.0s), 350/350 VUs, 287873 complete and 0 interrupted iterations
default   [  52% ] 350/350 VUs  1m02.0s/2m00.0s

running (1m03.0s), 350/350 VUs, 291257 complete and 0 interrupted iterations
default   [  52% ] 350/350 VUs  1m03.0s/2m00.0s

running (1m04.0s), 350/350 VUs, 295397 complete and 0 interrupted iterations
default   [  53% ] 350/350 VUs  1m04.0s/2m00.0s

running (1m05.0s), 350/350 VUs, 299339 complete and 0 interrupted iterations
default   [  54% ] 350/350 VUs  1m05.0s/2m00.0s

running (1m06.0s), 350/350 VUs, 303169 complete and 0 interrupted iterations
default   [  55% ] 350/350 VUs  1m06.0s/2m00.0s

running (1m07.0s), 350/350 VUs, 307700 complete and 0 interrupted iterations
default   [  56% ] 350/350 VUs  1m07.0s/2m00.0s

running (1m08.0s), 350/350 VUs, 310913 complete and 0 interrupted iterations
default   [  57% ] 350/350 VUs  1m08.0s/2m00.0s

running (1m09.0s), 350/350 VUs, 315364 complete and 0 interrupted iterations
default   [  57% ] 350/350 VUs  1m09.0s/2m00.0s

running (1m10.0s), 350/350 VUs, 319009 complete and 0 interrupted iterations
default   [  58% ] 350/350 VUs  1m10.0s/2m00.0s

running (1m11.0s), 350/350 VUs, 323560 complete and 0 interrupted iterations
default   [  59% ] 350/350 VUs  1m11.0s/2m00.0s

running (1m12.0s), 350/350 VUs, 327008 complete and 0 interrupted iterations
default   [  60% ] 350/350 VUs  1m12.0s/2m00.0s

running (1m13.0s), 350/350 VUs, 331339 complete and 0 interrupted iterations
default   [  61% ] 350/350 VUs  1m13.0s/2m00.0s

running (1m14.0s), 350/350 VUs, 335631 complete and 0 interrupted iterations
default   [  62% ] 350/350 VUs  1m14.0s/2m00.0s

running (1m15.0s), 350/350 VUs, 339966 complete and 0 interrupted iterations
default   [  62% ] 350/350 VUs  1m15.0s/2m00.0s

running (1m16.0s), 350/350 VUs, 345295 complete and 0 interrupted iterations
default   [  63% ] 350/350 VUs  1m16.0s/2m00.0s

running (1m17.0s), 350/350 VUs, 349846 complete and 0 interrupted iterations
default   [  64% ] 350/350 VUs  1m17.0s/2m00.0s

running (1m18.0s), 350/350 VUs, 353658 complete and 0 interrupted iterations
default   [  65% ] 350/350 VUs  1m18.0s/2m00.0s

running (1m19.0s), 350/350 VUs, 358043 complete and 0 interrupted iterations
default   [  66% ] 350/350 VUs  1m19.0s/2m00.0s

running (1m20.0s), 350/350 VUs, 362297 complete and 0 interrupted iterations
default   [  67% ] 350/350 VUs  1m20.0s/2m00.0s

running (1m21.0s), 350/350 VUs, 367226 complete and 0 interrupted iterations
default   [  67% ] 350/350 VUs  1m21.0s/2m00.0s

running (1m22.0s), 350/350 VUs, 372276 complete and 0 interrupted iterations
default   [  68% ] 350/350 VUs  1m22.0s/2m00.0s

running (1m23.0s), 350/350 VUs, 377211 complete and 0 interrupted iterations
default   [  69% ] 350/350 VUs  1m23.0s/2m00.0s

running (1m24.0s), 350/350 VUs, 382264 complete and 0 interrupted iterations
default   [  70% ] 350/350 VUs  1m24.0s/2m00.0s

running (1m25.0s), 350/350 VUs, 387512 complete and 0 interrupted iterations
default   [  71% ] 350/350 VUs  1m25.0s/2m00.0s

running (1m26.0s), 350/350 VUs, 391977 complete and 0 interrupted iterations
default   [  72% ] 350/350 VUs  1m26.0s/2m00.0s

running (1m27.0s), 350/350 VUs, 396744 complete and 0 interrupted iterations
default   [  72% ] 350/350 VUs  1m27.0s/2m00.0s

running (1m28.0s), 350/350 VUs, 400027 complete and 0 interrupted iterations
default   [  73% ] 350/350 VUs  1m28.0s/2m00.0s

running (1m29.0s), 350/350 VUs, 405623 complete and 0 interrupted iterations
default   [  74% ] 350/350 VUs  1m29.0s/2m00.0s

running (1m30.0s), 350/350 VUs, 411052 complete and 0 interrupted iterations
default   [  75% ] 350/350 VUs  1m30.0s/2m00.0s

running (1m31.0s), 350/350 VUs, 414808 complete and 0 interrupted iterations
default   [  76% ] 350/350 VUs  1m31.0s/2m00.0s

running (1m32.0s), 350/350 VUs, 419999 complete and 0 interrupted iterations
default   [  77% ] 350/350 VUs  1m32.0s/2m00.0s

running (1m33.0s), 350/350 VUs, 424621 complete and 0 interrupted iterations
default   [  77% ] 350/350 VUs  1m33.0s/2m00.0s

running (1m34.0s), 350/350 VUs, 430025 complete and 0 interrupted iterations
default   [  78% ] 350/350 VUs  1m34.0s/2m00.0s

running (1m35.0s), 350/350 VUs, 434101 complete and 0 interrupted iterations
default   [  79% ] 350/350 VUs  1m35.0s/2m00.0s

running (1m36.0s), 350/350 VUs, 438921 complete and 0 interrupted iterations
default   [  80% ] 350/350 VUs  1m36.0s/2m00.0s

running (1m37.0s), 350/350 VUs, 443676 complete and 0 interrupted iterations
default   [  81% ] 350/350 VUs  1m37.0s/2m00.0s

running (1m38.0s), 350/350 VUs, 448508 complete and 0 interrupted iterations
default   [  82% ] 350/350 VUs  1m38.0s/2m00.0s

running (1m39.0s), 350/350 VUs, 453574 complete and 0 interrupted iterations
default   [  82% ] 350/350 VUs  1m39.0s/2m00.0s

running (1m40.0s), 350/350 VUs, 459743 complete and 0 interrupted iterations
default   [  83% ] 350/350 VUs  1m40.0s/2m00.0s

running (1m41.0s), 350/350 VUs, 463862 complete and 0 interrupted iterations
default   [  84% ] 350/350 VUs  1m41.0s/2m00.0s

running (1m42.0s), 350/350 VUs, 468375 complete and 0 interrupted iterations
default   [  85% ] 350/350 VUs  1m42.0s/2m00.0s

running (1m43.0s), 350/350 VUs, 473334 complete and 0 interrupted iterations
default   [  86% ] 350/350 VUs  1m43.0s/2m00.0s

running (1m44.0s), 350/350 VUs, 478504 complete and 0 interrupted iterations
default   [  87% ] 350/350 VUs  1m44.0s/2m00.0s

running (1m45.0s), 350/350 VUs, 483652 complete and 0 interrupted iterations
default   [  87% ] 350/350 VUs  1m45.0s/2m00.0s

running (1m46.0s), 350/350 VUs, 489097 complete and 0 interrupted iterations
default   [  88% ] 350/350 VUs  1m46.0s/2m00.0s

running (1m47.0s), 350/350 VUs, 494184 complete and 0 interrupted iterations
default   [  89% ] 350/350 VUs  1m47.0s/2m00.0s

running (1m48.0s), 350/350 VUs, 499454 complete and 0 interrupted iterations
default   [  90% ] 350/350 VUs  1m48.0s/2m00.0s

running (1m49.0s), 350/350 VUs, 505231 complete and 0 interrupted iterations
default   [  91% ] 350/350 VUs  1m49.0s/2m00.0s

running (1m50.0s), 350/350 VUs, 510914 complete and 0 interrupted iterations
default   [  92% ] 350/350 VUs  1m50.0s/2m00.0s

running (1m51.0s), 350/350 VUs, 516764 complete and 0 interrupted iterations
default   [  92% ] 350/350 VUs  1m51.0s/2m00.0s

running (1m52.0s), 350/350 VUs, 522529 complete and 0 interrupted iterations
default   [  93% ] 350/350 VUs  1m52.0s/2m00.0s

running (1m53.0s), 350/350 VUs, 527636 complete and 0 interrupted iterations
default   [  94% ] 350/350 VUs  1m53.0s/2m00.0s

running (1m54.0s), 350/350 VUs, 532816 complete and 0 interrupted iterations
default   [  95% ] 350/350 VUs  1m54.0s/2m00.0s

running (1m55.0s), 350/350 VUs, 538838 complete and 0 interrupted iterations
default   [  96% ] 350/350 VUs  1m55.0s/2m00.0s

running (1m56.0s), 350/350 VUs, 543810 complete and 0 interrupted iterations
default   [  97% ] 350/350 VUs  1m56.0s/2m00.0s

running (1m57.0s), 350/350 VUs, 548030 complete and 0 interrupted iterations
default   [  97% ] 350/350 VUs  1m57.0s/2m00.0s

running (1m58.0s), 350/350 VUs, 552943 complete and 0 interrupted iterations
default   [  98% ] 350/350 VUs  1m58.0s/2m00.0s

running (1m59.0s), 350/350 VUs, 558678 complete and 0 interrupted iterations
default   [  99% ] 350/350 VUs  1m59.0s/2m00.0s

running (2m00.0s), 350/350 VUs, 563440 complete and 0 interrupted iterations
default   [ 100% ] 350/350 VUs  2m00.0s/2m00.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<500' p(95)=183.58ms
    ✓ 'p(99)<1000' p(99)=286.32ms

    http_req_failed
    ✓ 'rate<0.001' rate=0.00%

    http_reqs
    ✓ 'rate>2000' rate=4696.123515/s


  █ TOTAL RESULTS 

    checks_total.......................: 3382848 28176.74109/s
    checks_succeeded...................: 99.99%  3382633 out of 3382848
    checks_failed......................: 0.00%   215 out of 3382848

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✗ load: response time < 500ms
      ↳  99% — ✓ 563593 / ✗ 215
    ✓ load: response has body

    HTTP
    http_req_duration.......................................................: avg=65.06ms min=1.12ms med=48.99ms max=636.15ms p(90)=143.43ms p(95)=183.58ms
      { expected_response:true }............................................: avg=65.06ms min=1.12ms med=48.99ms max=636.15ms p(90)=143.43ms p(95)=183.58ms
    http_req_failed.........................................................: 0.00%  0 out of 563808
    http_reqs...............................................................: 563808 4696.123515/s

    EXECUTION
    iteration_duration......................................................: avg=65.2ms  min=1.2ms  med=49.11ms max=636.23ms p(90)=143.56ms p(95)=183.75ms
    iterations..............................................................: 563808 4696.123515/s
    vus.....................................................................: 350    min=12          max=350
    vus_max.................................................................: 350    min=350         max=350

    NETWORK
    data_received...........................................................: 151 MB 1.3 MB/s
    data_sent...............................................................: 227 MB 1.9 MB/s




running (2m00.1s), 000/350 VUs, 563808 complete and 0 interrupted iterations
default ✓ [ 100% ] 000/350 VUs  2m0s


## Stress Test - 2025-11-14 13:42:21


         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/stress-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 500 max VUs, 3m30s max duration (incl. graceful stop):
              * default: Up to 500 looping VUs for 3m0s over 4 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (0m01.0s), 002/500 VUs, 502 complete and 0 interrupted iterations
default   [   1% ] 002/500 VUs  0m01.0s/3m00.0s

running (0m02.0s), 004/500 VUs, 1678 complete and 0 interrupted iterations
default   [   1% ] 004/500 VUs  0m02.0s/3m00.0s

running (0m03.0s), 005/500 VUs, 3560 complete and 0 interrupted iterations
default   [   2% ] 005/500 VUs  0m03.0s/3m00.0s

running (0m04.0s), 007/500 VUs, 5672 complete and 0 interrupted iterations
default   [   2% ] 007/500 VUs  0m04.0s/3m00.0s

running (0m05.0s), 009/500 VUs, 8180 complete and 0 interrupted iterations
default   [   3% ] 009/500 VUs  0m05.0s/3m00.0s

running (0m06.0s), 010/500 VUs, 11058 complete and 0 interrupted iterations
default   [   3% ] 010/500 VUs  0m06.0s/3m00.0s

running (0m07.0s), 012/500 VUs, 13509 complete and 0 interrupted iterations
default   [   4% ] 012/500 VUs  0m07.0s/3m00.0s

running (0m08.0s), 013/500 VUs, 16528 complete and 0 interrupted iterations
default   [   4% ] 013/500 VUs  0m08.0s/3m00.0s

running (0m09.0s), 015/500 VUs, 19677 complete and 0 interrupted iterations
default   [   5% ] 015/500 VUs  0m09.0s/3m00.0s

running (0m10.0s), 017/500 VUs, 22597 complete and 0 interrupted iterations
default   [   6% ] 017/500 VUs  0m10.0s/3m00.0s

running (0m11.0s), 018/500 VUs, 26064 complete and 0 interrupted iterations
default   [   6% ] 018/500 VUs  0m11.0s/3m00.0s

running (0m12.0s), 020/500 VUs, 29748 complete and 0 interrupted iterations
default   [   7% ] 020/500 VUs  0m12.0s/3m00.0s

running (0m13.0s), 022/500 VUs, 33536 complete and 0 interrupted iterations
default   [   7% ] 022/500 VUs  0m13.0s/3m00.0s

running (0m14.0s), 023/500 VUs, 36467 complete and 0 interrupted iterations
default   [   8% ] 023/500 VUs  0m14.0s/3m00.0s

running (0m15.0s), 025/500 VUs, 40235 complete and 0 interrupted iterations
default   [   8% ] 025/500 VUs  0m15.0s/3m00.0s

running (0m16.0s), 027/500 VUs, 44031 complete and 0 interrupted iterations
default   [   9% ] 027/500 VUs  0m16.0s/3m00.0s

running (0m17.0s), 028/500 VUs, 47822 complete and 0 interrupted iterations
default   [   9% ] 028/500 VUs  0m17.0s/3m00.0s

running (0m18.0s), 030/500 VUs, 51710 complete and 0 interrupted iterations
default   [  10% ] 030/500 VUs  0m18.0s/3m00.0s

running (0m19.0s), 031/500 VUs, 55469 complete and 0 interrupted iterations
default   [  11% ] 031/500 VUs  0m19.0s/3m00.0s

running (0m20.0s), 033/500 VUs, 59246 complete and 0 interrupted iterations
default   [  11% ] 033/500 VUs  0m20.0s/3m00.0s

running (0m21.0s), 035/500 VUs, 63504 complete and 0 interrupted iterations
default   [  12% ] 035/500 VUs  0m21.0s/3m00.0s

running (0m22.0s), 036/500 VUs, 67727 complete and 0 interrupted iterations
default   [  12% ] 036/500 VUs  0m22.0s/3m00.0s

running (0m23.0s), 038/500 VUs, 71568 complete and 0 interrupted iterations
default   [  13% ] 038/500 VUs  0m23.0s/3m00.0s

running (0m24.0s), 040/500 VUs, 75035 complete and 0 interrupted iterations
default   [  13% ] 040/500 VUs  0m24.0s/3m00.0s

running (0m25.0s), 041/500 VUs, 79518 complete and 0 interrupted iterations
default   [  14% ] 041/500 VUs  0m25.0s/3m00.0s

running (0m26.0s), 043/500 VUs, 83871 complete and 0 interrupted iterations
default   [  14% ] 043/500 VUs  0m26.0s/3m00.0s

running (0m27.0s), 045/500 VUs, 87307 complete and 0 interrupted iterations
default   [  15% ] 045/500 VUs  0m27.0s/3m00.0s

running (0m28.0s), 046/500 VUs, 91568 complete and 0 interrupted iterations
default   [  16% ] 046/500 VUs  0m28.0s/3m00.0s

running (0m29.0s), 048/500 VUs, 95519 complete and 0 interrupted iterations
default   [  16% ] 048/500 VUs  0m29.0s/3m00.0s

running (0m30.0s), 049/500 VUs, 99748 complete and 0 interrupted iterations
default   [  17% ] 049/500 VUs  0m30.0s/3m00.0s

running (0m31.0s), 054/500 VUs, 102222 complete and 0 interrupted iterations
default   [  17% ] 054/500 VUs  0m31.0s/3m00.0s

running (0m32.0s), 059/500 VUs, 106557 complete and 0 interrupted iterations
default   [  18% ] 059/500 VUs  0m32.0s/3m00.0s

running (0m33.0s), 064/500 VUs, 111199 complete and 0 interrupted iterations
default   [  18% ] 064/500 VUs  0m33.0s/3m00.0s

running (0m34.0s), 069/500 VUs, 115731 complete and 0 interrupted iterations
default   [  19% ] 069/500 VUs  0m34.0s/3m00.0s

running (0m35.0s), 074/500 VUs, 120317 complete and 0 interrupted iterations
default   [  19% ] 074/500 VUs  0m35.0s/3m00.0s

running (0m36.0s), 079/500 VUs, 125026 complete and 0 interrupted iterations
default   [  20% ] 079/500 VUs  0m36.0s/3m00.0s

running (0m37.0s), 084/500 VUs, 129156 complete and 0 interrupted iterations
default   [  21% ] 084/500 VUs  0m37.0s/3m00.0s

running (0m38.0s), 089/500 VUs, 134052 complete and 0 interrupted iterations
default   [  21% ] 089/500 VUs  0m38.0s/3m00.0s

running (0m39.0s), 094/500 VUs, 138845 complete and 0 interrupted iterations
default   [  22% ] 094/500 VUs  0m39.0s/3m00.0s

running (0m40.0s), 099/500 VUs, 142974 complete and 0 interrupted iterations
default   [  22% ] 099/500 VUs  0m40.0s/3m00.0s

running (0m41.0s), 104/500 VUs, 148053 complete and 0 interrupted iterations
default   [  23% ] 104/500 VUs  0m41.0s/3m00.0s

running (0m42.0s), 109/500 VUs, 152889 complete and 0 interrupted iterations
default   [  23% ] 109/500 VUs  0m42.0s/3m00.0s

running (0m43.0s), 114/500 VUs, 157800 complete and 0 interrupted iterations
default   [  24% ] 114/500 VUs  0m43.0s/3m00.0s

running (0m44.0s), 119/500 VUs, 162959 complete and 0 interrupted iterations
default   [  24% ] 119/500 VUs  0m44.0s/3m00.0s

running (0m45.0s), 124/500 VUs, 168070 complete and 0 interrupted iterations
default   [  25% ] 124/500 VUs  0m45.0s/3m00.0s

running (0m46.0s), 129/500 VUs, 173179 complete and 0 interrupted iterations
default   [  26% ] 129/500 VUs  0m46.0s/3m00.0s

running (0m47.0s), 134/500 VUs, 178235 complete and 0 interrupted iterations
default   [  26% ] 134/500 VUs  0m47.0s/3m00.0s

running (0m48.0s), 139/500 VUs, 183615 complete and 0 interrupted iterations
default   [  27% ] 139/500 VUs  0m48.0s/3m00.0s

running (0m49.0s), 144/500 VUs, 188756 complete and 0 interrupted iterations
default   [  27% ] 144/500 VUs  0m49.0s/3m00.0s

running (0m50.0s), 149/500 VUs, 193763 complete and 0 interrupted iterations
default   [  28% ] 149/500 VUs  0m50.0s/3m00.0s

running (0m51.0s), 154/500 VUs, 199288 complete and 0 interrupted iterations
default   [  28% ] 154/500 VUs  0m51.0s/3m00.0s

running (0m52.0s), 159/500 VUs, 204250 complete and 0 interrupted iterations
default   [  29% ] 159/500 VUs  0m52.0s/3m00.0s

running (0m53.0s), 164/500 VUs, 209682 complete and 0 interrupted iterations
default   [  29% ] 164/500 VUs  0m53.0s/3m00.0s

running (0m54.0s), 169/500 VUs, 214967 complete and 0 interrupted iterations
default   [  30% ] 169/500 VUs  0m54.0s/3m00.0s

running (0m55.0s), 174/500 VUs, 219985 complete and 0 interrupted iterations
default   [  31% ] 174/500 VUs  0m55.0s/3m00.0s

running (0m56.0s), 179/500 VUs, 225241 complete and 0 interrupted iterations
default   [  31% ] 179/500 VUs  0m56.0s/3m00.0s

running (0m57.0s), 184/500 VUs, 230538 complete and 0 interrupted iterations
default   [  32% ] 184/500 VUs  0m57.0s/3m00.0s

running (0m58.0s), 189/500 VUs, 235644 complete and 0 interrupted iterations
default   [  32% ] 189/500 VUs  0m58.0s/3m00.0s

running (0m59.0s), 194/500 VUs, 240661 complete and 0 interrupted iterations
default   [  33% ] 194/500 VUs  0m59.0s/3m00.0s

running (1m00.0s), 199/500 VUs, 245035 complete and 0 interrupted iterations
default   [  33% ] 199/500 VUs  1m00.0s/3m00.0s

running (1m01.0s), 209/500 VUs, 250434 complete and 0 interrupted iterations
default   [  34% ] 209/500 VUs  1m01.0s/3m00.0s

running (1m02.0s), 219/500 VUs, 255292 complete and 0 interrupted iterations
default   [  34% ] 219/500 VUs  1m02.0s/3m00.0s

running (1m03.0s), 229/500 VUs, 261110 complete and 0 interrupted iterations
default   [  35% ] 229/500 VUs  1m03.0s/3m00.0s

running (1m04.0s), 239/500 VUs, 265699 complete and 0 interrupted iterations
default   [  36% ] 239/500 VUs  1m04.0s/3m00.0s

running (1m05.0s), 249/500 VUs, 271301 complete and 0 interrupted iterations
default   [  36% ] 249/500 VUs  1m05.0s/3m00.0s

running (1m06.0s), 259/500 VUs, 276752 complete and 0 interrupted iterations
default   [  37% ] 259/500 VUs  1m06.0s/3m00.0s

running (1m07.0s), 269/500 VUs, 282160 complete and 0 interrupted iterations
default   [  37% ] 269/500 VUs  1m07.0s/3m00.0s

running (1m08.0s), 279/500 VUs, 287860 complete and 0 interrupted iterations
default   [  38% ] 279/500 VUs  1m08.0s/3m00.0s

running (1m09.0s), 289/500 VUs, 293039 complete and 0 interrupted iterations
default   [  38% ] 289/500 VUs  1m09.0s/3m00.0s

running (1m10.0s), 299/500 VUs, 298276 complete and 0 interrupted iterations
default   [  39% ] 299/500 VUs  1m10.0s/3m00.0s

running (1m11.0s), 309/500 VUs, 302512 complete and 0 interrupted iterations
default   [  39% ] 309/500 VUs  1m11.0s/3m00.0s

running (1m12.0s), 319/500 VUs, 307540 complete and 0 interrupted iterations
default   [  40% ] 319/500 VUs  1m12.0s/3m00.0s

running (1m13.0s), 329/500 VUs, 313505 complete and 0 interrupted iterations
default   [  41% ] 329/500 VUs  1m13.0s/3m00.0s

running (1m14.0s), 339/500 VUs, 319109 complete and 0 interrupted iterations
default   [  41% ] 339/500 VUs  1m14.0s/3m00.0s

running (1m15.0s), 349/500 VUs, 324778 complete and 0 interrupted iterations
default   [  42% ] 349/500 VUs  1m15.0s/3m00.0s

running (1m16.0s), 359/500 VUs, 330169 complete and 0 interrupted iterations
default   [  42% ] 359/500 VUs  1m16.0s/3m00.0s

running (1m17.0s), 369/500 VUs, 336064 complete and 0 interrupted iterations
default   [  43% ] 369/500 VUs  1m17.0s/3m00.0s

running (1m18.0s), 379/500 VUs, 341994 complete and 0 interrupted iterations
default   [  43% ] 379/500 VUs  1m18.0s/3m00.0s

running (1m19.0s), 389/500 VUs, 346908 complete and 0 interrupted iterations
default   [  44% ] 389/500 VUs  1m19.0s/3m00.0s

running (1m20.0s), 399/500 VUs, 351606 complete and 0 interrupted iterations
default   [  44% ] 399/500 VUs  1m20.0s/3m00.0s

running (1m21.0s), 409/500 VUs, 357716 complete and 0 interrupted iterations
default   [  45% ] 409/500 VUs  1m21.0s/3m00.0s

running (1m22.0s), 419/500 VUs, 363434 complete and 0 interrupted iterations
default   [  46% ] 419/500 VUs  1m22.0s/3m00.0s

running (1m23.0s), 429/500 VUs, 368856 complete and 0 interrupted iterations
default   [  46% ] 429/500 VUs  1m23.0s/3m00.0s

running (1m24.0s), 439/500 VUs, 373922 complete and 0 interrupted iterations
default   [  47% ] 439/500 VUs  1m24.0s/3m00.0s

running (1m25.0s), 449/500 VUs, 379379 complete and 0 interrupted iterations
default   [  47% ] 449/500 VUs  1m25.0s/3m00.0s

running (1m26.0s), 459/500 VUs, 384351 complete and 0 interrupted iterations
default   [  48% ] 459/500 VUs  1m26.0s/3m00.0s

running (1m27.0s), 469/500 VUs, 388841 complete and 0 interrupted iterations
default   [  48% ] 469/500 VUs  1m27.0s/3m00.0s

running (1m28.0s), 479/500 VUs, 393336 complete and 0 interrupted iterations
default   [  49% ] 479/500 VUs  1m28.0s/3m00.0s

running (1m29.0s), 489/500 VUs, 397519 complete and 0 interrupted iterations
default   [  49% ] 489/500 VUs  1m29.0s/3m00.0s

running (1m30.0s), 499/500 VUs, 402174 complete and 0 interrupted iterations
default   [  50% ] 499/500 VUs  1m30.0s/3m00.0s

running (1m31.0s), 500/500 VUs, 405358 complete and 0 interrupted iterations
default   [  51% ] 500/500 VUs  1m31.0s/3m00.0s

running (1m32.0s), 500/500 VUs, 409522 complete and 0 interrupted iterations
default   [  51% ] 500/500 VUs  1m32.0s/3m00.0s

running (1m33.0s), 500/500 VUs, 414864 complete and 0 interrupted iterations
default   [  52% ] 500/500 VUs  1m33.0s/3m00.0s

running (1m34.0s), 500/500 VUs, 420153 complete and 0 interrupted iterations
default   [  52% ] 500/500 VUs  1m34.0s/3m00.0s

running (1m35.0s), 500/500 VUs, 424384 complete and 0 interrupted iterations
default   [  53% ] 500/500 VUs  1m35.0s/3m00.0s

running (1m36.0s), 500/500 VUs, 430060 complete and 0 interrupted iterations
default   [  53% ] 500/500 VUs  1m36.0s/3m00.0s

running (1m37.0s), 500/500 VUs, 434365 complete and 0 interrupted iterations
default   [  54% ] 500/500 VUs  1m37.0s/3m00.0s

running (1m38.0s), 500/500 VUs, 438988 complete and 0 interrupted iterations
default   [  54% ] 500/500 VUs  1m38.0s/3m00.0s

running (1m39.0s), 500/500 VUs, 444557 complete and 0 interrupted iterations
default   [  55% ] 500/500 VUs  1m39.0s/3m00.0s

running (1m40.0s), 500/500 VUs, 449669 complete and 0 interrupted iterations
default   [  56% ] 500/500 VUs  1m40.0s/3m00.0s

running (1m41.0s), 500/500 VUs, 453839 complete and 0 interrupted iterations
default   [  56% ] 500/500 VUs  1m41.0s/3m00.0s

running (1m42.0s), 500/500 VUs, 458429 complete and 0 interrupted iterations
default   [  57% ] 500/500 VUs  1m42.0s/3m00.0s

running (1m43.0s), 500/500 VUs, 462461 complete and 0 interrupted iterations
default   [  57% ] 500/500 VUs  1m43.0s/3m00.0s

running (1m44.0s), 500/500 VUs, 466167 complete and 0 interrupted iterations
default   [  58% ] 500/500 VUs  1m44.0s/3m00.0s

running (1m45.0s), 500/500 VUs, 471318 complete and 0 interrupted iterations
default   [  58% ] 500/500 VUs  1m45.0s/3m00.0s

running (1m46.0s), 500/500 VUs, 477149 complete and 0 interrupted iterations
default   [  59% ] 500/500 VUs  1m46.0s/3m00.0s

running (1m47.0s), 500/500 VUs, 481034 complete and 0 interrupted iterations
default   [  59% ] 500/500 VUs  1m47.0s/3m00.0s

running (1m48.0s), 500/500 VUs, 486043 complete and 0 interrupted iterations
default   [  60% ] 500/500 VUs  1m48.0s/3m00.0s

running (1m49.0s), 500/500 VUs, 491245 complete and 0 interrupted iterations
default   [  61% ] 500/500 VUs  1m49.0s/3m00.0s

running (1m50.0s), 500/500 VUs, 495082 complete and 0 interrupted iterations
default   [  61% ] 500/500 VUs  1m50.0s/3m00.0s

running (1m51.0s), 500/500 VUs, 499171 complete and 0 interrupted iterations
default   [  62% ] 500/500 VUs  1m51.0s/3m00.0s

running (1m52.0s), 500/500 VUs, 504626 complete and 0 interrupted iterations
default   [  62% ] 500/500 VUs  1m52.0s/3m00.0s

running (1m53.0s), 500/500 VUs, 509454 complete and 0 interrupted iterations
default   [  63% ] 500/500 VUs  1m53.0s/3m00.0s

running (1m54.0s), 500/500 VUs, 514319 complete and 0 interrupted iterations
default   [  63% ] 500/500 VUs  1m54.0s/3m00.0s

running (1m55.0s), 500/500 VUs, 519335 complete and 0 interrupted iterations
default   [  64% ] 500/500 VUs  1m55.0s/3m00.0s

running (1m56.0s), 500/500 VUs, 524357 complete and 0 interrupted iterations
default   [  64% ] 500/500 VUs  1m56.0s/3m00.0s

running (1m57.0s), 500/500 VUs, 528954 complete and 0 interrupted iterations
default   [  65% ] 500/500 VUs  1m57.0s/3m00.0s

running (1m58.0s), 500/500 VUs, 534391 complete and 0 interrupted iterations
default   [  66% ] 500/500 VUs  1m58.0s/3m00.0s

running (1m59.0s), 500/500 VUs, 540207 complete and 0 interrupted iterations
default   [  66% ] 500/500 VUs  1m59.0s/3m00.0s

running (2m00.0s), 500/500 VUs, 545216 complete and 0 interrupted iterations
default   [  67% ] 500/500 VUs  2m00.0s/3m00.0s

running (2m01.0s), 500/500 VUs, 549491 complete and 0 interrupted iterations
default   [  67% ] 500/500 VUs  2m01.0s/3m00.0s

running (2m02.0s), 500/500 VUs, 554338 complete and 0 interrupted iterations
default   [  68% ] 500/500 VUs  2m02.0s/3m00.0s

running (2m03.0s), 500/500 VUs, 558991 complete and 0 interrupted iterations
default   [  68% ] 500/500 VUs  2m03.0s/3m00.0s

running (2m04.0s), 500/500 VUs, 564726 complete and 0 interrupted iterations
default   [  69% ] 500/500 VUs  2m04.0s/3m00.0s

running (2m05.0s), 500/500 VUs, 570133 complete and 0 interrupted iterations
default   [  69% ] 500/500 VUs  2m05.0s/3m00.0s

running (2m06.0s), 500/500 VUs, 575049 complete and 0 interrupted iterations
default   [  70% ] 500/500 VUs  2m06.0s/3m00.0s

running (2m07.0s), 500/500 VUs, 579083 complete and 0 interrupted iterations
default   [  71% ] 500/500 VUs  2m07.0s/3m00.0s

running (2m08.0s), 500/500 VUs, 583517 complete and 0 interrupted iterations
default   [  71% ] 500/500 VUs  2m08.0s/3m00.0s

running (2m09.0s), 500/500 VUs, 589125 complete and 0 interrupted iterations
default   [  72% ] 500/500 VUs  2m09.0s/3m00.0s

running (2m10.0s), 500/500 VUs, 593139 complete and 0 interrupted iterations
default   [  72% ] 500/500 VUs  2m10.0s/3m00.0s

running (2m11.0s), 500/500 VUs, 597356 complete and 0 interrupted iterations
default   [  73% ] 500/500 VUs  2m11.0s/3m00.0s

running (2m12.0s), 500/500 VUs, 603005 complete and 0 interrupted iterations
default   [  73% ] 500/500 VUs  2m12.0s/3m00.0s

running (2m13.0s), 500/500 VUs, 608221 complete and 0 interrupted iterations
default   [  74% ] 500/500 VUs  2m13.0s/3m00.0s

running (2m14.0s), 500/500 VUs, 613199 complete and 0 interrupted iterations
default   [  74% ] 500/500 VUs  2m14.0s/3m00.0s

running (2m15.0s), 500/500 VUs, 617595 complete and 0 interrupted iterations
default   [  75% ] 500/500 VUs  2m15.0s/3m00.0s

running (2m16.0s), 500/500 VUs, 622952 complete and 0 interrupted iterations
default   [  76% ] 500/500 VUs  2m16.0s/3m00.0s

running (2m17.0s), 500/500 VUs, 627381 complete and 0 interrupted iterations
default   [  76% ] 500/500 VUs  2m17.0s/3m00.0s

running (2m17.9s), 500/500 VUs, 631566 complete and 0 interrupted iterations
default   [  77% ] 500/500 VUs  2m18.0s/3m00.0s

running (2m19.0s), 500/500 VUs, 636228 complete and 0 interrupted iterations
default   [  77% ] 500/500 VUs  2m19.0s/3m00.0s

running (2m20.0s), 500/500 VUs, 640191 complete and 0 interrupted iterations
default   [  78% ] 500/500 VUs  2m20.0s/3m00.0s

running (2m21.0s), 500/500 VUs, 645873 complete and 0 interrupted iterations
default   [  78% ] 500/500 VUs  2m21.0s/3m00.0s

running (2m21.9s), 500/500 VUs, 650431 complete and 0 interrupted iterations
default   [  79% ] 500/500 VUs  2m22.0s/3m00.0s

running (2m22.9s), 500/500 VUs, 655023 complete and 0 interrupted iterations
default   [  79% ] 500/500 VUs  2m23.0s/3m00.0s

running (2m23.9s), 500/500 VUs, 659240 complete and 0 interrupted iterations
default   [  80% ] 500/500 VUs  2m24.0s/3m00.0s

running (2m25.0s), 500/500 VUs, 664440 complete and 0 interrupted iterations
default   [  81% ] 500/500 VUs  2m25.0s/3m00.0s

running (2m26.0s), 500/500 VUs, 669767 complete and 0 interrupted iterations
default   [  81% ] 500/500 VUs  2m26.0s/3m00.0s

running (2m27.0s), 500/500 VUs, 673139 complete and 0 interrupted iterations
default   [  82% ] 500/500 VUs  2m27.0s/3m00.0s

running (2m28.0s), 500/500 VUs, 677762 complete and 0 interrupted iterations
default   [  82% ] 500/500 VUs  2m28.0s/3m00.0s

running (2m28.9s), 500/500 VUs, 683239 complete and 0 interrupted iterations
default   [  83% ] 500/500 VUs  2m29.0s/3m00.0s

running (2m29.9s), 500/500 VUs, 687749 complete and 0 interrupted iterations
default   [  83% ] 500/500 VUs  2m30.0s/3m00.0s

running (2m31.0s), 500/500 VUs, 691896 complete and 0 interrupted iterations
default   [  84% ] 500/500 VUs  2m31.0s/3m00.0s

running (2m31.9s), 500/500 VUs, 696107 complete and 0 interrupted iterations
default   [  84% ] 500/500 VUs  2m32.0s/3m00.0s

running (2m32.9s), 500/500 VUs, 700500 complete and 0 interrupted iterations
default   [  85% ] 500/500 VUs  2m33.0s/3m00.0s

running (2m33.9s), 500/500 VUs, 704999 complete and 0 interrupted iterations
default   [  86% ] 500/500 VUs  2m34.0s/3m00.0s

running (2m34.9s), 500/500 VUs, 710516 complete and 0 interrupted iterations
default   [  86% ] 500/500 VUs  2m35.0s/3m00.0s

running (2m36.0s), 500/500 VUs, 714910 complete and 0 interrupted iterations
default   [  87% ] 500/500 VUs  2m36.0s/3m00.0s

running (2m37.0s), 500/500 VUs, 718907 complete and 0 interrupted iterations
default   [  87% ] 500/500 VUs  2m37.0s/3m00.0s

running (2m37.9s), 500/500 VUs, 723083 complete and 0 interrupted iterations
default   [  88% ] 500/500 VUs  2m38.0s/3m00.0s

running (2m39.0s), 500/500 VUs, 728421 complete and 0 interrupted iterations
default   [  88% ] 500/500 VUs  2m39.0s/3m00.0s

running (2m40.0s), 500/500 VUs, 733151 complete and 0 interrupted iterations
default   [  89% ] 500/500 VUs  2m40.0s/3m00.0s

running (2m40.9s), 500/500 VUs, 737955 complete and 0 interrupted iterations
default   [  89% ] 500/500 VUs  2m41.0s/3m00.0s

running (2m41.9s), 500/500 VUs, 743028 complete and 0 interrupted iterations
default   [  90% ] 500/500 VUs  2m42.0s/3m00.0s

running (2m42.9s), 500/500 VUs, 748073 complete and 0 interrupted iterations
default   [  91% ] 500/500 VUs  2m43.0s/3m00.0s

running (2m43.9s), 500/500 VUs, 753346 complete and 0 interrupted iterations
default   [  91% ] 500/500 VUs  2m44.0s/3m00.0s

running (2m44.9s), 500/500 VUs, 758008 complete and 0 interrupted iterations
default   [  92% ] 500/500 VUs  2m45.0s/3m00.0s

running (2m45.9s), 500/500 VUs, 762365 complete and 0 interrupted iterations
default   [  92% ] 500/500 VUs  2m46.0s/3m00.0s

running (2m47.0s), 500/500 VUs, 766819 complete and 0 interrupted iterations
default   [  93% ] 500/500 VUs  2m47.0s/3m00.0s

running (2m47.9s), 500/500 VUs, 771584 complete and 0 interrupted iterations
default   [  93% ] 500/500 VUs  2m48.0s/3m00.0s

running (2m48.9s), 500/500 VUs, 776723 complete and 0 interrupted iterations
default   [  94% ] 500/500 VUs  2m49.0s/3m00.0s

running (2m49.9s), 500/500 VUs, 781527 complete and 0 interrupted iterations
default   [  94% ] 500/500 VUs  2m50.0s/3m00.0s

running (2m50.9s), 500/500 VUs, 786498 complete and 0 interrupted iterations
default   [  95% ] 500/500 VUs  2m51.0s/3m00.0s

running (2m51.9s), 500/500 VUs, 790700 complete and 0 interrupted iterations
default   [  96% ] 500/500 VUs  2m52.0s/3m00.0s

running (2m52.9s), 500/500 VUs, 794254 complete and 0 interrupted iterations
default   [  96% ] 500/500 VUs  2m53.0s/3m00.0s

running (2m53.9s), 500/500 VUs, 798849 complete and 0 interrupted iterations
default   [  97% ] 500/500 VUs  2m54.0s/3m00.0s

running (2m54.9s), 500/500 VUs, 802884 complete and 0 interrupted iterations
default   [  97% ] 500/500 VUs  2m55.0s/3m00.0s

running (2m55.9s), 500/500 VUs, 807717 complete and 0 interrupted iterations
default   [  98% ] 500/500 VUs  2m56.0s/3m00.0s

running (2m56.9s), 500/500 VUs, 812432 complete and 0 interrupted iterations
default   [  98% ] 500/500 VUs  2m57.0s/3m00.0s

running (2m57.9s), 500/500 VUs, 817628 complete and 0 interrupted iterations
default   [  99% ] 500/500 VUs  2m58.0s/3m00.0s

running (2m59.0s), 500/500 VUs, 822402 complete and 0 interrupted iterations
default   [  99% ] 500/500 VUs  2m59.0s/3m00.0s

running (2m59.9s), 500/500 VUs, 825915 complete and 0 interrupted iterations
default   [ 100% ] 500/500 VUs  3m00.0s/3m00.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<2000' p(95)=253.47ms
    ✓ 'p(99)<5000' p(99)=355.22ms

    http_req_failed
    ✓ 'rate<0.05' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 4960350 27542.702224/s
    checks_succeeded...................: 100.00% 4960350 out of 4960350
    checks_failed......................: 0.00%   0 out of 4960350

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ stress: response time < 2000ms
    ✓ stress: response has body

    HTTP
    http_req_duration.......................................................: avg=72.43ms min=1.02ms med=36.71ms max=928.89ms p(90)=195.53ms p(95)=253.47ms
      { expected_response:true }............................................: avg=72.43ms min=1.02ms med=36.71ms max=928.89ms p(90)=195.53ms p(95)=253.47ms
    http_req_failed.........................................................: 0.00%  0 out of 826725
    http_reqs...............................................................: 826725 4590.450371/s

    EXECUTION
    iteration_duration......................................................: avg=72.57ms min=1.09ms med=36.85ms max=928.99ms p(90)=195.7ms  p(95)=253.62ms
    iterations..............................................................: 826725 4590.450371/s
    vus.....................................................................: 500    min=2           max=500
    vus_max.................................................................: 500    min=500         max=500

    NETWORK
    data_received...........................................................: 222 MB 1.2 MB/s
    data_sent...............................................................: 333 MB 1.8 MB/s




running (3m00.1s), 000/500 VUs, 826725 complete and 0 interrupted iterations
default ✓ [ 100% ] 000/500 VUs  3m0s


## Spike Test - 2025-11-14 13:44:56


         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/spike-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 500 max VUs, 3m0s max duration (incl. graceful stop):
              * default: Up to 500 looping VUs for 2m30s over 7 stages (gracefulRampDown: 30s, gracefulStop: 30s)


running (0m01.0s), 003/500 VUs, 739 complete and 0 interrupted iterations
default   [   1% ] 003/500 VUs  0m01.0s/2m30.0s

running (0m02.0s), 005/500 VUs, 2403 complete and 0 interrupted iterations
default   [   1% ] 005/500 VUs  0m02.0s/2m30.0s

running (0m03.0s), 008/500 VUs, 4695 complete and 0 interrupted iterations
default   [   2% ] 008/500 VUs  0m03.0s/2m30.0s

running (0m04.0s), 010/500 VUs, 7648 complete and 0 interrupted iterations
default   [   3% ] 010/500 VUs  0m04.0s/2m30.0s

running (0m05.0s), 013/500 VUs, 10677 complete and 0 interrupted iterations
default   [   3% ] 013/500 VUs  0m05.0s/2m30.0s

running (0m06.0s), 015/500 VUs, 14173 complete and 0 interrupted iterations
default   [   4% ] 015/500 VUs  0m06.0s/2m30.0s

running (0m07.0s), 018/500 VUs, 17601 complete and 0 interrupted iterations
default   [   5% ] 018/500 VUs  0m07.0s/2m30.0s

running (0m08.0s), 020/500 VUs, 21475 complete and 0 interrupted iterations
default   [   5% ] 020/500 VUs  0m08.0s/2m30.0s

running (0m09.0s), 022/500 VUs, 24910 complete and 0 interrupted iterations
default   [   6% ] 022/500 VUs  0m09.0s/2m30.0s

running (0m10.0s), 025/500 VUs, 28850 complete and 0 interrupted iterations
default   [   7% ] 025/500 VUs  0m10.0s/2m30.0s

running (0m11.0s), 027/500 VUs, 32695 complete and 0 interrupted iterations
default   [   7% ] 027/500 VUs  0m11.0s/2m30.0s

running (0m12.0s), 030/500 VUs, 36818 complete and 0 interrupted iterations
default   [   8% ] 030/500 VUs  0m12.0s/2m30.0s

running (0m13.0s), 032/500 VUs, 40726 complete and 0 interrupted iterations
default   [   9% ] 032/500 VUs  0m13.0s/2m30.0s

running (0m14.0s), 035/500 VUs, 44678 complete and 0 interrupted iterations
default   [   9% ] 035/500 VUs  0m14.0s/2m30.0s

running (0m15.0s), 037/500 VUs, 48566 complete and 0 interrupted iterations
default   [  10% ] 037/500 VUs  0m15.0s/2m30.0s

running (0m16.0s), 040/500 VUs, 52932 complete and 0 interrupted iterations
default   [  11% ] 040/500 VUs  0m16.0s/2m30.0s

running (0m17.0s), 042/500 VUs, 57305 complete and 0 interrupted iterations
default   [  11% ] 042/500 VUs  0m17.0s/2m30.0s

running (0m18.0s), 045/500 VUs, 61624 complete and 0 interrupted iterations
default   [  12% ] 045/500 VUs  0m18.0s/2m30.0s

running (0m19.0s), 047/500 VUs, 65184 complete and 0 interrupted iterations
default   [  13% ] 047/500 VUs  0m19.0s/2m30.0s

running (0m20.0s), 049/500 VUs, 69551 complete and 0 interrupted iterations
default   [  13% ] 049/500 VUs  0m20.0s/2m30.0s

running (0m21.0s), 071/500 VUs, 74014 complete and 0 interrupted iterations
default   [  14% ] 071/500 VUs  0m21.0s/2m30.0s

running (0m22.0s), 094/500 VUs, 77724 complete and 0 interrupted iterations
default   [  15% ] 094/500 VUs  0m22.0s/2m30.0s

running (0m23.0s), 116/500 VUs, 82082 complete and 0 interrupted iterations
default   [  15% ] 116/500 VUs  0m23.0s/2m30.0s

running (0m24.0s), 139/500 VUs, 86469 complete and 0 interrupted iterations
default   [  16% ] 139/500 VUs  0m24.0s/2m30.0s

running (0m25.0s), 161/500 VUs, 91135 complete and 0 interrupted iterations
default   [  17% ] 161/500 VUs  0m25.0s/2m30.0s

running (0m26.0s), 184/500 VUs, 96112 complete and 0 interrupted iterations
default   [  17% ] 184/500 VUs  0m26.0s/2m30.0s

running (0m27.0s), 206/500 VUs, 99553 complete and 0 interrupted iterations
default   [  18% ] 206/500 VUs  0m27.0s/2m30.0s

running (0m28.0s), 229/500 VUs, 105047 complete and 0 interrupted iterations
default   [  19% ] 229/500 VUs  0m28.0s/2m30.0s

running (0m29.0s), 251/500 VUs, 110679 complete and 0 interrupted iterations
default   [  19% ] 251/500 VUs  0m29.0s/2m30.0s

running (0m30.0s), 274/500 VUs, 116019 complete and 0 interrupted iterations
default   [  20% ] 274/500 VUs  0m30.0s/2m30.0s

running (0m31.0s), 296/500 VUs, 121354 complete and 0 interrupted iterations
default   [  21% ] 296/500 VUs  0m31.0s/2m30.0s

running (0m32.0s), 319/500 VUs, 126346 complete and 0 interrupted iterations
default   [  21% ] 319/500 VUs  0m32.0s/2m30.0s

running (0m33.0s), 341/500 VUs, 129833 complete and 0 interrupted iterations
default   [  22% ] 341/500 VUs  0m33.0s/2m30.0s

running (0m34.0s), 364/500 VUs, 135525 complete and 0 interrupted iterations
default   [  23% ] 364/500 VUs  0m34.0s/2m30.0s

running (0m35.0s), 386/500 VUs, 140377 complete and 0 interrupted iterations
default   [  23% ] 386/500 VUs  0m35.0s/2m30.0s

running (0m36.0s), 409/500 VUs, 143872 complete and 0 interrupted iterations
default   [  24% ] 409/500 VUs  0m36.0s/2m30.0s

running (0m37.0s), 431/500 VUs, 147994 complete and 0 interrupted iterations
default   [  25% ] 431/500 VUs  0m37.0s/2m30.0s

running (0m38.0s), 454/500 VUs, 153290 complete and 0 interrupted iterations
default   [  25% ] 454/500 VUs  0m38.0s/2m30.0s

running (0m39.0s), 476/500 VUs, 159463 complete and 0 interrupted iterations
default   [  26% ] 476/500 VUs  0m39.0s/2m30.0s

running (0m40.0s), 499/500 VUs, 164385 complete and 0 interrupted iterations
default   [  27% ] 499/500 VUs  0m40.0s/2m30.0s

running (0m41.0s), 483/500 VUs, 169960 complete and 0 interrupted iterations
default   [  27% ] 483/500 VUs  0m41.0s/2m30.0s

running (0m42.0s), 457/500 VUs, 174630 complete and 0 interrupted iterations
default   [  28% ] 457/500 VUs  0m42.0s/2m30.0s

running (0m43.0s), 436/500 VUs, 178516 complete and 0 interrupted iterations
default   [  29% ] 436/500 VUs  0m43.0s/2m30.0s

running (0m44.0s), 413/500 VUs, 184748 complete and 0 interrupted iterations
default   [  29% ] 413/500 VUs  0m44.0s/2m30.0s

running (0m45.0s), 392/500 VUs, 190036 complete and 0 interrupted iterations
default   [  30% ] 392/500 VUs  0m45.0s/2m30.0s

running (0m46.0s), 367/500 VUs, 195124 complete and 0 interrupted iterations
default   [  31% ] 367/500 VUs  0m46.0s/2m30.0s

running (0m47.0s), 348/500 VUs, 200742 complete and 0 interrupted iterations
default   [  31% ] 348/500 VUs  0m47.0s/2m30.0s

running (0m48.0s), 322/500 VUs, 206163 complete and 0 interrupted iterations
default   [  32% ] 322/500 VUs  0m48.0s/2m30.0s

running (0m49.0s), 299/500 VUs, 210382 complete and 0 interrupted iterations
default   [  33% ] 299/500 VUs  0m49.0s/2m30.0s

running (0m50.0s), 277/500 VUs, 216260 complete and 0 interrupted iterations
default   [  33% ] 277/500 VUs  0m50.0s/2m30.0s

running (0m51.0s), 254/500 VUs, 221976 complete and 0 interrupted iterations
default   [  34% ] 254/500 VUs  0m51.0s/2m30.0s

running (0m52.0s), 232/500 VUs, 227696 complete and 0 interrupted iterations
default   [  35% ] 232/500 VUs  0m52.0s/2m30.0s

running (0m53.0s), 210/500 VUs, 233157 complete and 0 interrupted iterations
default   [  35% ] 210/500 VUs  0m53.0s/2m30.0s

running (0m54.0s), 187/500 VUs, 239029 complete and 0 interrupted iterations
default   [  36% ] 187/500 VUs  0m54.0s/2m30.0s

running (0m55.0s), 165/500 VUs, 244310 complete and 0 interrupted iterations
default   [  37% ] 165/500 VUs  0m55.0s/2m30.0s

running (0m56.0s), 141/500 VUs, 249401 complete and 0 interrupted iterations
default   [  37% ] 141/500 VUs  0m56.0s/2m30.0s

running (0m57.0s), 120/500 VUs, 254631 complete and 0 interrupted iterations
default   [  38% ] 120/500 VUs  0m57.0s/2m30.0s

running (0m58.0s), 097/500 VUs, 259905 complete and 0 interrupted iterations
default   [  39% ] 097/500 VUs  0m58.0s/2m30.0s

running (0m59.0s), 077/500 VUs, 264218 complete and 0 interrupted iterations
default   [  39% ] 077/500 VUs  0m59.0s/2m30.0s

running (1m00.0s), 052/500 VUs, 268281 complete and 0 interrupted iterations
default   [  40% ] 052/500 VUs  1m00.0s/2m30.0s

running (1m01.0s), 071/500 VUs, 272409 complete and 0 interrupted iterations
default   [  41% ] 071/500 VUs  1m01.0s/2m30.0s

running (1m02.0s), 094/500 VUs, 276441 complete and 0 interrupted iterations
default   [  41% ] 094/500 VUs  1m02.0s/2m30.0s

running (1m03.0s), 116/500 VUs, 280943 complete and 0 interrupted iterations
default   [  42% ] 116/500 VUs  1m03.0s/2m30.0s

running (1m04.0s), 139/500 VUs, 285147 complete and 0 interrupted iterations
default   [  43% ] 139/500 VUs  1m04.0s/2m30.0s

running (1m05.0s), 161/500 VUs, 289976 complete and 0 interrupted iterations
default   [  43% ] 161/500 VUs  1m05.0s/2m30.0s

running (1m06.0s), 184/500 VUs, 294145 complete and 0 interrupted iterations
default   [  44% ] 184/500 VUs  1m06.0s/2m30.0s

running (1m07.0s), 206/500 VUs, 299080 complete and 0 interrupted iterations
default   [  45% ] 206/500 VUs  1m07.0s/2m30.0s

running (1m08.0s), 229/500 VUs, 303192 complete and 0 interrupted iterations
default   [  45% ] 229/500 VUs  1m08.0s/2m30.0s

running (1m09.0s), 251/500 VUs, 308452 complete and 0 interrupted iterations
default   [  46% ] 251/500 VUs  1m09.0s/2m30.0s

running (1m10.0s), 274/500 VUs, 313647 complete and 0 interrupted iterations
default   [  47% ] 274/500 VUs  1m10.0s/2m30.0s

running (1m11.0s), 296/500 VUs, 319605 complete and 0 interrupted iterations
default   [  47% ] 296/500 VUs  1m11.0s/2m30.0s

running (1m12.0s), 319/500 VUs, 325436 complete and 0 interrupted iterations
default   [  48% ] 319/500 VUs  1m12.0s/2m30.0s

running (1m13.0s), 341/500 VUs, 331087 complete and 0 interrupted iterations
default   [  49% ] 341/500 VUs  1m13.0s/2m30.0s

running (1m14.0s), 364/500 VUs, 336611 complete and 0 interrupted iterations
default   [  49% ] 364/500 VUs  1m14.0s/2m30.0s

running (1m15.0s), 386/500 VUs, 341786 complete and 0 interrupted iterations
default   [  50% ] 386/500 VUs  1m15.0s/2m30.0s

running (1m16.0s), 409/500 VUs, 347138 complete and 0 interrupted iterations
default   [  51% ] 409/500 VUs  1m16.0s/2m30.0s

running (1m17.0s), 431/500 VUs, 352557 complete and 0 interrupted iterations
default   [  51% ] 431/500 VUs  1m17.0s/2m30.0s

running (1m18.0s), 454/500 VUs, 357252 complete and 0 interrupted iterations
default   [  52% ] 454/500 VUs  1m18.0s/2m30.0s

running (1m19.0s), 476/500 VUs, 361673 complete and 0 interrupted iterations
default   [  53% ] 476/500 VUs  1m19.0s/2m30.0s

running (1m20.0s), 499/500 VUs, 367463 complete and 0 interrupted iterations
default   [  53% ] 499/500 VUs  1m20.0s/2m30.0s

running (1m21.0s), 481/500 VUs, 373305 complete and 0 interrupted iterations
default   [  54% ] 481/500 VUs  1m21.0s/2m30.0s

running (1m22.0s), 459/500 VUs, 377299 complete and 0 interrupted iterations
default   [  55% ] 459/500 VUs  1m22.0s/2m30.0s

running (1m23.0s), 435/500 VUs, 381323 complete and 0 interrupted iterations
default   [  55% ] 435/500 VUs  1m23.0s/2m30.0s

running (1m24.0s), 417/500 VUs, 385749 complete and 0 interrupted iterations
default   [  56% ] 417/500 VUs  1m24.0s/2m30.0s

running (1m25.0s), 392/500 VUs, 390418 complete and 0 interrupted iterations
default   [  57% ] 392/500 VUs  1m25.0s/2m30.0s

running (1m26.0s), 368/500 VUs, 395547 complete and 0 interrupted iterations
default   [  57% ] 368/500 VUs  1m26.0s/2m30.0s

running (1m27.0s), 344/500 VUs, 398475 complete and 0 interrupted iterations
default   [  58% ] 344/500 VUs  1m27.0s/2m30.0s

running (1m28.0s), 322/500 VUs, 403594 complete and 0 interrupted iterations
default   [  59% ] 322/500 VUs  1m28.0s/2m30.0s

running (1m29.0s), 299/500 VUs, 409007 complete and 0 interrupted iterations
default   [  59% ] 299/500 VUs  1m29.0s/2m30.0s

running (1m30.0s), 277/500 VUs, 414124 complete and 0 interrupted iterations
default   [  60% ] 277/500 VUs  1m30.0s/2m30.0s

running (1m31.0s), 254/500 VUs, 419248 complete and 0 interrupted iterations
default   [  61% ] 254/500 VUs  1m31.0s/2m30.0s

running (1m32.0s), 232/500 VUs, 424739 complete and 0 interrupted iterations
default   [  61% ] 232/500 VUs  1m32.0s/2m30.0s

running (1m33.0s), 209/500 VUs, 430403 complete and 0 interrupted iterations
default   [  62% ] 209/500 VUs  1m33.0s/2m30.0s

running (1m34.0s), 186/500 VUs, 436086 complete and 0 interrupted iterations
default   [  63% ] 186/500 VUs  1m34.0s/2m30.0s

running (1m35.0s), 164/500 VUs, 441075 complete and 0 interrupted iterations
default   [  63% ] 164/500 VUs  1m35.0s/2m30.0s

running (1m36.0s), 142/500 VUs, 446661 complete and 0 interrupted iterations
default   [  64% ] 142/500 VUs  1m36.0s/2m30.0s

running (1m37.0s), 119/500 VUs, 451510 complete and 0 interrupted iterations
default   [  65% ] 119/500 VUs  1m37.0s/2m30.0s

running (1m38.0s), 096/500 VUs, 455387 complete and 0 interrupted iterations
default   [  65% ] 096/500 VUs  1m38.0s/2m30.0s

running (1m39.0s), 075/500 VUs, 459913 complete and 0 interrupted iterations
default   [  66% ] 075/500 VUs  1m39.0s/2m30.0s

running (1m40.0s), 051/500 VUs, 464246 complete and 0 interrupted iterations
default   [  67% ] 051/500 VUs  1m40.0s/2m30.0s

running (1m41.0s), 071/500 VUs, 468771 complete and 0 interrupted iterations
default   [  67% ] 071/500 VUs  1m41.0s/2m30.0s

running (1m42.0s), 094/500 VUs, 473550 complete and 0 interrupted iterations
default   [  68% ] 094/500 VUs  1m42.0s/2m30.0s

running (1m43.0s), 116/500 VUs, 478321 complete and 0 interrupted iterations
default   [  69% ] 116/500 VUs  1m43.0s/2m30.0s

running (1m44.0s), 139/500 VUs, 483597 complete and 0 interrupted iterations
default   [  69% ] 139/500 VUs  1m44.0s/2m30.0s

running (1m45.0s), 161/500 VUs, 489026 complete and 0 interrupted iterations
default   [  70% ] 161/500 VUs  1m45.0s/2m30.0s

running (1m46.0s), 184/500 VUs, 494116 complete and 0 interrupted iterations
default   [  71% ] 184/500 VUs  1m46.0s/2m30.0s

running (1m47.0s), 206/500 VUs, 499927 complete and 0 interrupted iterations
default   [  71% ] 206/500 VUs  1m47.0s/2m30.0s

running (1m48.0s), 229/500 VUs, 505630 complete and 0 interrupted iterations
default   [  72% ] 229/500 VUs  1m48.0s/2m30.0s

running (1m49.0s), 251/500 VUs, 511032 complete and 0 interrupted iterations
default   [  73% ] 251/500 VUs  1m49.0s/2m30.0s

running (1m50.0s), 274/500 VUs, 516626 complete and 0 interrupted iterations
default   [  73% ] 274/500 VUs  1m50.0s/2m30.0s

running (1m51.0s), 296/500 VUs, 522274 complete and 0 interrupted iterations
default   [  74% ] 296/500 VUs  1m51.0s/2m30.0s

running (1m52.0s), 319/500 VUs, 527231 complete and 0 interrupted iterations
default   [  75% ] 319/500 VUs  1m52.0s/2m30.0s

running (1m53.0s), 341/500 VUs, 532274 complete and 0 interrupted iterations
default   [  75% ] 341/500 VUs  1m53.0s/2m30.0s

running (1m54.0s), 364/500 VUs, 537267 complete and 0 interrupted iterations
default   [  76% ] 364/500 VUs  1m54.0s/2m30.0s

running (1m55.0s), 386/500 VUs, 541481 complete and 0 interrupted iterations
default   [  77% ] 386/500 VUs  1m55.0s/2m30.0s

running (1m56.0s), 409/500 VUs, 545986 complete and 0 interrupted iterations
default   [  77% ] 409/500 VUs  1m56.0s/2m30.0s

running (1m57.0s), 431/500 VUs, 551559 complete and 0 interrupted iterations
default   [  78% ] 431/500 VUs  1m57.0s/2m30.0s

running (1m58.0s), 454/500 VUs, 557425 complete and 0 interrupted iterations
default   [  79% ] 454/500 VUs  1m58.0s/2m30.0s

running (1m59.0s), 476/500 VUs, 562205 complete and 0 interrupted iterations
default   [  79% ] 476/500 VUs  1m59.0s/2m30.0s

running (2m00.0s), 499/500 VUs, 566110 complete and 0 interrupted iterations
default   [  80% ] 499/500 VUs  2m00.0s/2m30.0s

running (2m01.0s), 488/500 VUs, 570806 complete and 0 interrupted iterations
default   [  81% ] 488/500 VUs  2m01.0s/2m30.0s

running (2m02.0s), 472/500 VUs, 576580 complete and 0 interrupted iterations
default   [  81% ] 472/500 VUs  2m02.0s/2m30.0s

running (2m03.0s), 456/500 VUs, 581572 complete and 0 interrupted iterations
default   [  82% ] 456/500 VUs  2m03.0s/2m30.0s

running (2m04.0s), 442/500 VUs, 586594 complete and 0 interrupted iterations
default   [  83% ] 442/500 VUs  2m04.0s/2m30.0s

running (2m05.0s), 427/500 VUs, 591150 complete and 0 interrupted iterations
default   [  83% ] 427/500 VUs  2m05.0s/2m30.0s

running (2m06.0s), 412/500 VUs, 597262 complete and 0 interrupted iterations
default   [  84% ] 412/500 VUs  2m06.0s/2m30.0s

running (2m07.0s), 397/500 VUs, 602913 complete and 0 interrupted iterations
default   [  85% ] 397/500 VUs  2m07.0s/2m30.0s

running (2m08.0s), 385/500 VUs, 606872 complete and 0 interrupted iterations
default   [  85% ] 385/500 VUs  2m08.0s/2m30.0s

running (2m09.0s), 368/500 VUs, 612238 complete and 0 interrupted iterations
default   [  86% ] 368/500 VUs  2m09.0s/2m30.0s

running (2m10.0s), 351/500 VUs, 618128 complete and 0 interrupted iterations
default   [  87% ] 351/500 VUs  2m10.0s/2m30.0s

running (2m11.0s), 338/500 VUs, 623685 complete and 0 interrupted iterations
default   [  87% ] 338/500 VUs  2m11.0s/2m30.0s

running (2m12.0s), 322/500 VUs, 630203 complete and 0 interrupted iterations
default   [  88% ] 322/500 VUs  2m12.0s/2m30.0s

running (2m13.0s), 306/500 VUs, 635397 complete and 0 interrupted iterations
default   [  89% ] 306/500 VUs  2m13.0s/2m30.0s

running (2m14.0s), 291/500 VUs, 641286 complete and 0 interrupted iterations
default   [  89% ] 291/500 VUs  2m14.0s/2m30.0s

running (2m15.0s), 277/500 VUs, 646618 complete and 0 interrupted iterations
default   [  90% ] 277/500 VUs  2m15.0s/2m30.0s

running (2m16.0s), 262/500 VUs, 651341 complete and 0 interrupted iterations
default   [  91% ] 262/500 VUs  2m16.0s/2m30.0s

running (2m17.0s), 247/500 VUs, 656566 complete and 0 interrupted iterations
default   [  91% ] 247/500 VUs  2m17.0s/2m30.0s

running (2m18.0s), 231/500 VUs, 662341 complete and 0 interrupted iterations
default   [  92% ] 231/500 VUs  2m18.0s/2m30.0s

running (2m19.0s), 217/500 VUs, 667819 complete and 0 interrupted iterations
default   [  93% ] 217/500 VUs  2m19.0s/2m30.0s

running (2m20.0s), 202/500 VUs, 673215 complete and 0 interrupted iterations
default   [  93% ] 202/500 VUs  2m20.0s/2m30.0s

running (2m21.0s), 186/500 VUs, 679240 complete and 0 interrupted iterations
default   [  94% ] 186/500 VUs  2m21.0s/2m30.0s

running (2m22.0s), 171/500 VUs, 684543 complete and 0 interrupted iterations
default   [  95% ] 171/500 VUs  2m22.0s/2m30.0s

running (2m23.0s), 156/500 VUs, 688770 complete and 0 interrupted iterations
default   [  95% ] 156/500 VUs  2m23.0s/2m30.0s

running (2m24.0s), 142/500 VUs, 694224 complete and 0 interrupted iterations
default   [  96% ] 142/500 VUs  2m24.0s/2m30.0s

running (2m25.0s), 126/500 VUs, 698539 complete and 0 interrupted iterations
default   [  97% ] 126/500 VUs  2m25.0s/2m30.0s

running (2m26.0s), 111/500 VUs, 703433 complete and 0 interrupted iterations
default   [  97% ] 111/500 VUs  2m26.0s/2m30.0s

running (2m27.0s), 096/500 VUs, 706923 complete and 0 interrupted iterations
default   [  98% ] 096/500 VUs  2m27.0s/2m30.0s

running (2m28.0s), 081/500 VUs, 712313 complete and 0 interrupted iterations
default   [  99% ] 081/500 VUs  2m28.0s/2m30.0s

running (2m29.0s), 066/500 VUs, 716922 complete and 0 interrupted iterations
default   [  99% ] 066/500 VUs  2m29.0s/2m30.0s

running (2m30.0s), 051/500 VUs, 721960 complete and 0 interrupted iterations
default   [ 100% ] 051/500 VUs  2m30.0s/2m30.0s


  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<2000' p(95)=159.03ms

    http_req_failed
    ✓ 'rate<0.02' rate=0.00%


  █ TOTAL RESULTS 

    checks_total.......................: 4333128 28887.209559/s
    checks_succeeded...................: 100.00% 4333128 out of 4333128
    checks_failed......................: 0.00%   0 out of 4333128

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ spike: response time < 2000ms
    ✓ spike: response has body

    HTTP
    http_req_duration.......................................................: avg=50.15ms min=1.12ms med=31.88ms max=659.84ms p(90)=115ms    p(95)=159.03ms
      { expected_response:true }............................................: avg=50.15ms min=1.12ms med=31.88ms max=659.84ms p(90)=115ms    p(95)=159.03ms
    http_req_failed.........................................................: 0.00%  0 out of 722188
    http_reqs...............................................................: 722188 4814.534926/s

    EXECUTION
    iteration_duration......................................................: avg=50.28ms min=1.2ms  med=32.01ms max=659.94ms p(90)=115.15ms p(95)=159.16ms
    iterations..............................................................: 722188 4814.534926/s
    vus.....................................................................: 51     min=3           max=499
    vus_max.................................................................: 500    min=500         max=500

    NETWORK
    data_received...........................................................: 194 MB 1.3 MB/s
    data_sent...............................................................: 291 MB 1.9 MB/s




running (2m30.0s), 000/500 VUs, 722188 complete and 0 interrupted iterations
default ✓ [ 100% ] 000/500 VUs  2m30s

