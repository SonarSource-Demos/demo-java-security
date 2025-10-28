# Demo - Java Security        
[![Quality Gate Status](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=alert_status&token=squ_1e4f3504bdc994f093721895e070abe7c11b1632)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Maintainability Rating](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=sqale_rating&token=squ_1e4f3504bdc994f093721895e070abe7c11b1632)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Reliability Rating](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=reliability_rating&token=squ_1e4f3504bdc994f093721895e070abe7c11b1632)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Security Rating](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=security_rating&token=squ_1e4f3504bdc994f093721895e070abe7c11b1632)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Security Hotspots](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=security_hotspots&token=squ_1e4f3504bdc994f093721895e070abe7c11b1632)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security)

## Use case
This example demonstrates:
- **Vulnerabilities** (including SQL Injection, XSS, Path Traversal)
- **Security Hotspots** (hardcoded passwords, weak crypto, unsafe deserialization)
- **Taint Analysis Issues** (complex data flow vulnerabilities)
- **Code Quality Issues** (dead code, duplicated code, magic numbers)

Perfect for **SonarQube sales demos** showcasing comprehensive security analysis capabilities.

## SonarQube Issues Included

### 🔴 Security Vulnerabilities
- **SQL Injection**: Direct string concatenation in SQL queries
- **Cross-Site Scripting (XSS)**: Unescaped user input in HTML output
- **Path Traversal**: User input used in file paths
- **Command Injection**: User input passed to system commands
- **Deserialization Vulnerabilities**: Unsafe object deserialization
- **Information Disclosure**: Sensitive data in error messages

### 🟡 Security Hotspots
- **Hardcoded Passwords**: Plaintext passwords in source code
- **Weak Cryptographic Algorithms**: Insecure hashing methods
- **Unsafe Deserialization**: ObjectInputStream without validation

### 🔵 Code Quality Issues
- **Null Pointer Vulnerabilities**: Missing null checks
- **Dead Code**: Unreachable code and unused methods
- **Code Duplication**: Repeated logic across methods
- **Magic Numbers**: Hardcoded numeric values
- **Long Methods**: Methods with too many responsibilities
- **Too Many Parameters**: Methods with excessive parameters
- **Generic Exception Handling**: Catching Exception instead of specific types

### 🟢 Taint Analysis Examples
- **Complex Data Flow**: User input flowing through multiple methods to SQL queries
- **String Concatenation**: Taint propagation through string building
- **Session Data**: Untrusted session data used without validation
- **File Operations**: User input affecting file system operations

## Usage

Run `./run.sh`

This will:
- Delete the project key **training:java-security** if it exists in SonarQube (to start from a scratch)
- Run `mvn clean verify sonar:sonar` to re-create the project

Project consists of multiple servlet classes with intentional vulnerabilities and code quality issues perfect for demonstrating SonarQube's analysis capabilities.

## Custom security configuration 
At the bottom of the class you see a bunch of methods that demonstrate custom injections.
- The method without sanitization (`doSomething()`) has an injection vulnerability
- The method with custom sanitization (`doSomethingSanitized()`) has no vulnerability

The custom security configuration file is in the root directory [here](s3649JavaSqlInjectionConfig.json)

## Sales Demo Tips

This project is specifically designed for SonarQube sales demonstrations:

1. **Show Taint Analysis**: Demonstrate how SonarQube tracks data flow from user input to vulnerable sinks
2. **Highlight Security Hotspots**: Show the difference between vulnerabilities and security hotspots
3. **Code Quality**: Demonstrate maintainability and reliability issues
4. **Custom Rules**: Show how custom sources, sanitizers, and sinks can be configured
5. **Real-time Analysis**: Run analysis during the demo to show immediate feedback
