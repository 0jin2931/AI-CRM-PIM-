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
	implementation ("org.springframework.boot:spring-boot-starter-webflux")// WebClient를 사용하기 위한 WebFlux 의존성 추가
	implementation ("org.springframework.boot:spring-boot-starter-web")
	implementation ("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	implementation ("com.fasterxml.jackson.core:jackson-databind")

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
    
    // 2. OS 환경변수를 테스트 환경으로 전달 (def 대신 val 사용)
    val dbPassword = System.getenv("DB_PASSWORD") ?: "Dudwls@2931"
    environment("DB_PASSWORD", dbPassword)
    
    val geminiApiKey = System.getenv("GEMINI_API_KEY") ?: "mock-key"
    environment("GEMINI_API_KEY", geminiApiKey)
}
