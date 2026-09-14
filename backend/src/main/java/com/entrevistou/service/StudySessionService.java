package com.entrevistou.service;

import com.entrevistou.dto.*;
import com.entrevistou.model.StudyTopic;
import com.entrevistou.repository.StudyTopicRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudySessionService {
    private final StudyTopicRepository topicRepository;
    private final Map<String, SessionDefinition> sessions;

    public StudySessionService(StudyTopicRepository topicRepository) {
        this.topicRepository = topicRepository;
        this.sessions = buildSessions();
    }

    public StudySessionDto getSession(String slug) {
        SessionDefinition s = Optional.ofNullable(sessions.get(slug))
                .orElseThrow(() -> new NoSuchElementException("Sessão de estudo não encontrada"));
        StudyTopic topic = topicRepository.findByTitle(s.topicTitle())
                .orElseThrow(() -> new NoSuchElementException("Tópico não encontrado"));
        return new StudySessionDto(slug, s.category(), s.title(), s.subtitle(), s.minutes(), topic.getMastery(),
                s.sections(), s.questions().stream().map(PrivateQuestion::publicView).toList());
    }

    public StudySessionResultDto submit(String slug, SubmitSessionRequest request) {
        SessionDefinition s = Optional.ofNullable(sessions.get(slug))
                .orElseThrow(() -> new NoSuchElementException("Sessão de estudo não encontrada"));
        Map<String, String> answers = request.answers() == null ? Map.of() : request.answers();
        int correct = 0;
        List<QuestionResultDto> results = new ArrayList<>();
        for (PrivateQuestion q : s.questions()) {
            boolean ok = q.correctOptionId().equals(answers.get(q.id()));
            if (ok) correct++;
            results.add(new QuestionResultDto(q.id(), ok, q.correctOptionId(), q.explanation()));
        }
        int total = s.questions().size();
        int score = (int)Math.round(correct * 100.0 / total);
        StudyTopic topic = topicRepository.findByTitle(s.topicTitle())
                .orElseThrow(() -> new NoSuchElementException("Tópico não encontrado"));
        int previous = topic.getMastery();
        int delta = score >= 80 ? 6 : score >= 60 ? 3 : score >= 40 ? 1 : -2;
        int next = Math.max(0, Math.min(100, previous + delta));
        topic.setMastery(next);
        topicRepository.save(topic);
        String message = score >= 80 ? "Excelente. Você demonstrou uma base forte para entrevista."
                : score >= 60 ? "Boa base. Revise os pontos destacados antes de avançar."
                : "Revise o conteúdo e repita a sessão para consolidar os fundamentos.";
        return new StudySessionResultDto(correct, total, score, previous, next, message, results);
    }

    private Map<String, SessionDefinition> buildSessions() {
        Map<String, SessionDefinition> m = new LinkedHashMap<>();

        m.put("java-core-collections", session("Java Core", "Collections & Generics",
                "Collections, Generics & Concorrência",
                "Revise os fundamentos de Java que mais aparecem em entrevistas Senior.", 30,
                List.of(
                        section("collections", "1. Collections", "Escolha a estrutura de dados pela semântica e pelo custo operacional.",
                                List.of("ArrayList oferece acesso indexado O(1) amortizado.", "HashMap depende de hashCode/equals consistentes.", "Set representa unicidade; Map representa associação chave/valor."),
                                "Em entrevista, explique trade-offs em vez de apenas citar classes."),
                        section("generics", "2. Generics", "Generics aumentam segurança de tipo em tempo de compilação.",
                                List.of("Use bounded wildcards para APIs flexíveis.", "PECS: Producer Extends, Consumer Super.", "Type erasure remove parâmetros genéricos em runtime."),
                                "PECS é uma resposta clássica para perguntas sobre wildcards."),
                        section("concurrency", "3. Concorrência", "Código thread-safe evita condições de corrida e visibilidade inconsistente.",
                                List.of("ConcurrentHashMap é preferível a sincronizar um HashMap inteiro.", "volatile trata visibilidade, não atomicidade composta.", "CompletableFuture ajuda a compor tarefas assíncronas."),
                                "Diferencie atomicidade, visibilidade e exclusão mútua.")),
                List.of(
                        q("j1", "Qual estrutura é mais adequada para lookup por chave em média O(1)?", "Fundamental", "b",
                                "HashMap oferece lookup médio O(1) com hash bem distribuído.", "ArrayList", "HashMap", "TreeSet", "LinkedList"),
                        q("j2", "O que significa PECS em Generics?", "Intermediário", "a",
                                "PECS significa Producer Extends, Consumer Super.", "Producer Extends, Consumer Super", "Public Extends, Class Super", "Primitive Equals, Class Safe", "Producer Equals, Consumer Static"),
                        q("j3", "volatile garante que contador++ seja atômico?", "Senior", "c",
                                "Não. volatile garante visibilidade, mas contador++ envolve leitura, soma e escrita.", "Sim, sempre", "Sim, em 64 bits", "Não", "Somente com HashMap"))));

        m.put("spring-boot-senior", session("Spring", "Spring Boot & DI", "Spring Boot para entrevistas Senior",
                "Entenda DI, REST, JPA e transações com foco em decisões de arquitetura.", 30,
                List.of(
                        section("di", "1. Dependency Injection", "O container gerencia criação e composição de objetos.",
                                List.of("Prefira constructor injection.", "Beans singleton devem ser stateless quando possível.", "@Configuration e auto-configuration reduzem wiring manual."),
                                "Explique por que DI melhora testabilidade e desacoplamento."),
                        section("rest", "2. REST", "Controllers devem orquestrar HTTP, não concentrar regra de negócio.",
                                List.of("Use DTOs na fronteira da API.", "Valide entrada com Bean Validation.", "Mapeie erros de forma consistente com @ControllerAdvice."),
                                "Fale de idempotência em PUT/DELETE e versionamento de API."),
                        section("tx", "3. JPA & Transactions", "Transações definem unidade atômica de trabalho.",
                                List.of("Lazy loading fora da transação pode gerar LazyInitializationException.", "N+1 deve ser tratado com fetch join/entity graph/projeções.", "@Transactional usa proxy e possui implicações em self-invocation."),
                                "N+1 e limites transacionais são perguntas frequentes em Senior.")),
                List.of(
                        q("s1", "Qual forma de injeção é geralmente preferida?", "Fundamental", "b", "Constructor injection explicita dependências e facilita testes.", "Field injection", "Constructor injection", "Reflection manual", "Static injection"),
                        q("s2", "Qual problema ocorre ao carregar uma coleção lazy após fechar a sessão?", "Intermediário", "a", "Pode ocorrer LazyInitializationException.", "LazyInitializationException", "StackOverflowError sempre", "Deadlock SQL", "ClassNotFoundException"),
                        q("s3", "Por que self-invocation pode ignorar @Transactional?", "Senior", "d", "A chamada interna não passa pelo proxy Spring que aplica o interceptor transacional.", "Porque JPA não suporta métodos internos", "Porque transações exigem Kafka", "Porque @Transactional só funciona em controllers", "Porque a chamada não passa pelo proxy"))));

        m.put("kafka-consumer-groups-offsets", session("Kafka", "Consumer Groups & Offsets", "Kafka: Consumer Groups & Offsets",
                "Distribuição de trabalho, commits, rebalancing, retry e idempotência.", 28,
                List.of(
                        section("groups", "1. Consumer Groups", "Consumers do mesmo grupo dividem partitions.",
                                List.of("Cada partition pertence a no máximo um consumer do grupo.", "Consumers excedentes ficam ociosos.", "Grupos diferentes leem o mesmo tópico independentemente."), "Relacione partitions com paralelismo máximo."),
                        section("offsets", "2. Offsets", "Offset representa a posição de leitura por grupo e partition.",
                                List.of("Commit registra progresso.", "Commit após processamento favorece at-least-once.", "Idempotência protege contra duplicidades."), "Explique commit, retry e DLQ como um conjunto."),
                        section("rebalance", "3. Rebalancing", "Mudanças no grupo redistribuem partitions.",
                                List.of("Rebalances podem pausar consumo.", "Cooperative rebalancing reduz interrupções.", "Static membership pode aumentar estabilidade."), "Mais consumers não significa automaticamente mais throughput.")),
                List.of(
                        q("k1", "Um tópico tem 4 partitions e o grupo tem 6 consumers. Quantos processam simultaneamente?", "Fundamental", "b", "No máximo 4 consumers, um por partition.", "2", "4", "6", "24"),
                        q("k2", "Qual técnica é crítica em at-least-once?", "Intermediário", "c", "Idempotência evita efeitos duplicados em reentregas.", "Desabilitar offsets", "Uma única partition", "Idempotência", "Commit antes de processar"),
                        q("k3", "O que ocorre em um rebalance?", "Intermediário", "a", "Partitions são redistribuídas entre consumers do grupo.", "Redistribuição de partitions", "Exclusão do tópico", "Recriação do cluster", "Pausa permanente dos producers"))));

        m.put("sql-indexes-transactions", session("SQL", "Indexes & Transactions", "SQL: Índices & Transações",
                "Aprenda a justificar índices, níveis de isolamento e decisões de modelagem.", 30,
                List.of(
                        section("indexes", "1. Índices", "Índices aceleram leitura ao custo de espaço e manutenção em escrita.",
                                List.of("Índice composto segue a ordem das colunas.", "Alta seletividade tende a favorecer índice.", "EXPLAIN ajuda a validar o plano de execução."), "Não diga 'índice deixa tudo rápido'; explique custos."),
                        section("transactions", "2. Transações", "ACID define garantias de consistência e isolamento.",
                                List.of("READ COMMITTED evita dirty reads.", "REPEATABLE READ protege leituras repetidas.", "SERIALIZABLE maximiza isolamento com maior custo."), "Relacione isolation level com anomalias."),
                        section("locking", "3. Locks", "Concorrência de banco exige equilíbrio entre consistência e throughput.",
                                List.of("Optimistic locking usa versão e detecta conflito.", "Pessimistic locking bloqueia antecipadamente.", "Deadlocks exigem ordem consistente e transações curtas."), "Explique quando escolher optimistic vs pessimistic.")),
                List.of(
                        q("q1", "Qual ferramenta mostra o plano de execução de uma query?", "Fundamental", "a", "EXPLAIN mostra como o banco pretende executar a consulta.", "EXPLAIN", "VACUUM ONLY", "COMMIT", "GRANT"),
                        q("q2", "Qual isolamento impede dirty reads no PostgreSQL?", "Intermediário", "b", "READ COMMITTED já impede dirty reads.", "READ UNCOMMITTED", "READ COMMITTED", "NONE", "AUTOCOMMIT"),
                        q("q3", "Optimistic locking normalmente usa o quê?", "Senior", "d", "Uma coluna de versão permite detectar atualização concorrente.", "Table scan", "Trigger de delete", "Full table lock", "Coluna de versão"))));

        m.put("system-design-scalability", session("System Design", "Scalability & Resilience", "System Design: Escalabilidade & Resiliência",
                "Treine o raciocínio esperado em entrevistas de arquitetura distribuída.", 35,
                List.of(
                        section("requirements", "1. Requisitos", "Comece clarificando escala, latência, disponibilidade e consistência.",
                                List.of("Separe requisitos funcionais e não funcionais.", "Estime ordem de grandeza de tráfego e armazenamento.", "Defina o caminho crítico antes de desenhar componentes."), "A entrevista avalia processo, não apenas o diagrama final."),
                        section("scale", "2. Escalabilidade", "Escala horizontal distribui carga entre instâncias.",
                                List.of("Load balancers distribuem requisições.", "Cache reduz pressão em dependências.", "Sharding distribui dados quando um nó deixa de ser suficiente."), "Sempre mencione invalidação e consistência de cache."),
                        section("resilience", "3. Resiliência", "Falhas parciais são normais em sistemas distribuídos.",
                                List.of("Timeouts evitam espera indefinida.", "Retries precisam de backoff e jitter.", "Circuit breaker limita cascatas de falha."), "Retry sem idempotência pode duplicar efeitos.")),
                List.of(
                        q("d1", "Qual é o primeiro passo saudável em uma entrevista de System Design?", "Fundamental", "c", "Clarificar requisitos evita projetar o sistema errado.", "Escolher banco", "Desenhar Kafka", "Clarificar requisitos", "Criar classes Java"),
                        q("d2", "Qual padrão ajuda a impedir cascata de falhas?", "Intermediário", "a", "Circuit breaker interrompe chamadas para dependências degradadas.", "Circuit breaker", "Factory", "Singleton", "DTO"),
                        q("d3", "Por que retry deve usar backoff e jitter?", "Senior", "b", "Reduz tempestades sincronizadas de novas tentativas.", "Para aumentar payload", "Para reduzir retry storms", "Para remover logs", "Para evitar autenticação"))));

        m.put("mock-interview-senior-backend", session("Mock Interview", "Senior Backend Interview", "Mock Interview: Java Senior Backend",
                "Simulação objetiva para treinar respostas técnicas de nível Senior.", 25,
                List.of(
                        section("structure", "1. Estruture sua resposta", "Responda com contexto, decisão, trade-off e resultado.",
                                List.of("Comece pela premissa principal.", "Mostre alternativas consideradas.", "Conecte teoria com experiência prática."), "Evite respostas enciclopédicas; priorize clareza e decisão."),
                        section("depth", "2. Profundidade Senior", "Senioridade aparece na capacidade de discutir consequências.",
                                List.of("Fale de observabilidade e operação.", "Considere falhas e concorrência.", "Explique como mediria sucesso em produção."), "Use exemplos concretos sempre que possível."),
                        section("communication", "3. Comunicação", "Pensamento estruturado vale tanto quanto acertar o detalhe técnico.",
                                List.of("Confirme a pergunta.", "Declare suposições.", "Peça dados adicionais quando necessário."), "Uma boa resposta torna seu raciocínio auditável.")),
                List.of(
                        q("m1", "Ao explicar uma decisão arquitetural, o que mais demonstra senioridade?", "Intermediário", "b", "Trade-offs e consequências mostram julgamento técnico.", "Citar muitas ferramentas", "Explicar trade-offs", "Falar mais rápido", "Evitar perguntas"),
                        q("m2", "Se a pergunta estiver ambígua, qual atitude é melhor?", "Fundamental", "a", "Clarificar premissas reduz risco de responder ao problema errado.", "Clarificar requisitos", "Inventar requisitos", "Ignorar contexto", "Trocar de assunto"),
                        q("m3", "O que diferencia uma resposta Senior sobre produção?", "Senior", "d", "Senior considera operação, métricas, falhas e impacto real.", "Somente sintaxe", "Somente framework", "Somente diagrama", "Operação, métricas e falhas"))));

        return m;
    }

    private SessionDefinition session(String category, String topicTitle, String title, String subtitle, int minutes,
                                      List<StudySectionDto> sections, List<PrivateQuestion> questions) {
        return new SessionDefinition(category, topicTitle, title, subtitle, minutes, sections, questions);
    }
    private StudySectionDto section(String id, String title, String summary, List<String> bullets, String tip) {
        return new StudySectionDto(id, title, summary, bullets, tip);
    }
    private PrivateQuestion q(String id, String prompt, String difficulty, String correct, String explanation,
                              String a, String b, String c, String d) {
        return new PrivateQuestion(id, prompt, difficulty,
                List.of(new QuestionOptionDto("a", a), new QuestionOptionDto("b", b), new QuestionOptionDto("c", c), new QuestionOptionDto("d", d)),
                correct, explanation);
    }

    private record SessionDefinition(String category, String topicTitle, String title, String subtitle, int minutes,
                                     List<StudySectionDto> sections, List<PrivateQuestion> questions) {}
    private record PrivateQuestion(String id, String prompt, String difficulty, List<QuestionOptionDto> options,
                                   String correctOptionId, String explanation) {
        StudyQuestionDto publicView() { return new StudyQuestionDto(id, prompt, difficulty, options); }
    }
}
