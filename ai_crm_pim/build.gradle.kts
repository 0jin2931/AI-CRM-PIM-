plugins {
	java
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-webflux") // WebClient를 사용하기 위한 WebFlux 의존성
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")

	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")

	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
    
    // 1. 콘솔창 한글 깨짐 방지 (괄호와 쌍따옴표 사용)
    systemProperty("file.encoding", "UTF-8")
    
    // 2. DB_PASSWORD 등 OS 환경변수는 테스트 JVM에 그대로 상속됨 (비밀번호를 코드에 하드코딩하지 말 것)
    //    GEMINI_API_KEY가 없으면 컨텍스트 로딩만 되도록 mock 값 사용
    if (System.getenv("GEMINI_API_KEY") == null) {
        environment("GEMINI_API_KEY", "mock-key")
    }
}
