# AI-CRM-PIM-
매장 관리자가 챗봇에게 "최근 한 달간 100만 원 이상 구매한 VIP 고객 목록 뽑아주고, 이들에게 신상품 할인 안내 문자 발송해 줘" 또는 "현재 재고가 10개 미만인 상품들 발주 리스트 만들어 줘"라고 자연어로 요청하면, AI가 의도를 파악해 백엔드에서 복잡한 DB 쿼리를 수행하고 결과를 반환하는 시스템.


    sequenceDiagram
    participant Admin as 매장 관리자
    participant Spring as Spring Boot (Backend)
    participant OpenAI as OpenAI API
    participant DB as MySQL DB

    Admin->>Spring: 자연어 요청 ("재고 5개 미만인 그래픽카드 찾아줘")
    Spring->>OpenAI: 프롬프트 + 자연어 전송
    OpenAI-->>Spring: JSON 검색 조건 반환 {category: "GPU", stock_less_than: 5}
    Spring->>Spring: JSON 파싱 및 QueryDSL 동적 쿼리 생성
    Spring->>DB: 조회 쿼리 실행
    DB-->>Spring: 조회 결과 데이터
    Spring-->>Admin: 정리된 데이터 및 텍스트 응답 반환


    erDiagram
    MEMBER {
        BigInt id PK
        String name
        String email
        String grade "회원 등급 (NORMAL, VIP)"
        DateTime created_at
    }
    
    PRODUCT {
        BigInt id PK
        String category "CPU, RAM, GPU 등"
        String name "상품명"
        Int price "가격"
        Int stock_quantity "재고 수량"
        BigInt version "낙관적 락을 위한 버전 필드"
    }
    
    ORDERS {
        BigInt id PK
        BigInt member_id FK
        String status "주문 상태 (ORDERED, CANCELED)"
        Int total_price "총 결제 금액"
        DateTime order_date
    }
    
    ORDER_ITEM {
        BigInt id PK
        BigInt order_id FK
        BigInt product_id FK
        Int order_price "주문 당시 가격"
        Int count "주문 수량"
    }

    MEMBER ||--o{ ORDERS : places
    ORDERS ||--|{ ORDER_ITEM : contains
    PRODUCT ||--o{ ORDER_ITEM : included_in
