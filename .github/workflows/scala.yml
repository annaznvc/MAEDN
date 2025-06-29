name: Scala CI with Coverage
on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Check out the repository
      uses: actions/checkout@v3
      
    - name: Set up Java
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
        
    - name: Set up sbt
      uses: coursier/setup-action@v1
      with:
        apps: sbt
        
    - name: Cache sbt dependencies
      uses: actions/cache@v3
      with:
        path: |
          ~/.ivy2/cache
          ~/.sbt
          ~/.coursier
        key: ${{ runner.os }}-sbt-${{ hashFiles('**/*.sbt') }}-${{ hashFiles('**/project/build.properties') }}
        restore-keys: |
          ${{ runner.os }}-sbt-
          
    - name: Set up virtual display for JavaFX
      run: |
        sudo apt-get update
        sudo apt-get install -y xvfb
        export DISPLAY=:99
        Xvfb :99 -screen 0 1024x768x24 > /dev/null 2>&1 &
        sleep 3
        
    - name: Run Tests with Coverage
      run: |
        export DISPLAY=:99
        sbt -Djava.awt.headless=true -Dtestfx.robot=glass -Dtestfx.headless=true -Dprism.order=sw clean coverage test
        sbt coverageReport
        sbt coverageAggregate
      env:
        DISPLAY: :99
        
    - name: Upload Coverage Report
      if: success()
      uses: actions/upload-artifact@v4
      with:
        name: coverage-report
        path: target/scala-*/scoverage-report
        
    - name: Upload Coverage Report to Coveralls
      if: success()
      run: sbt coveralls
      env:
        COVERALLS_REPO_TOKEN: ${{ secrets.COVERALLS_REPO_TOKEN }}
        
    - name: Upload Coverage to Codecov
      if: success()
      uses: codecov/codecov-action@v3
      with:
        files: target/scala-*/scoverage-report/scoverage.xml
        token: ${{ secrets.CODECOV_TOKEN }}
        fail_ci_if_error: false
