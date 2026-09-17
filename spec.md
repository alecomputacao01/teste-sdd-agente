# Especificação (SDD) — cliente

> Gerado no modo **assistido por agente** (Platform Engineering 2.0) do template `poc-java-arquitetura-ddd`.
> Segue o fluxo `constitution → specify → plan → tasks → implement` (mesmo estilo *spec-kit* usado no
> projeto `poc-backstage-archunit-sdd` que gerou este repositório). A "constitution" já vem resolvida —
> ver `CLAUDE.md`. Este arquivo cobre **specify**: o que este serviço faz, não como.
>
> **Antes de pedir `/plan`:** resolva todo marcador de pendência abaixo (edite este arquivo
> diretamente, ou peça ao agente para refinar via `/specify`) e rode
> `scripts/sdd/check-spec-ready.sh spec.md` — ele falha enquanto sobrar algum.

## 1. Contexto e Objetivo
REQ-01: Encapsulamento de Estado e Ciclo de Vida do Assinante (RDM / Máquina de Estados)Descrição: A entidade Cliente (Raiz do Agregado) deve gerenciar de forma autônoma suas transições de status (Ativo, Suspenso por Inadimplência, Suspenso a Pedido, Cancelado). Nenhuma aplicação consumidora (como o CRM) pode alterar o status do cliente diretamente por propriedades (getters/setters).Regras de Negócio (Invariantes):O cliente só pode passar para o estado Suspenso por Inadimplência se houver pelo menos uma fatura com mais de 15 dias de atraso informada pelo contexto de Billing.Um cliente no estado Cancelado não pode reativar serviços ou sofrer novas cobranças; ele deve ser tratado como histórico ou fluxo de reaquisição.Exposição para Consumidores: O modelo deve expor um método de consulta que retorne o estado atual e as ações permitidas para aquele momento (ex: cliente.ObterAcoesDisponiveis()). O CRM e o Portal consumirão essa API para renderizar dinamicamente quais botões (Reativar, Suspender, Alterar) estarão visíveis ou bloqueados para o atendente ou usuário. REQ-02: Isolamento de Lógica Externa via Domain Services (Elegibilidade e Crédito)Descrição: A tomada de decisão sobre a concessão de novas linhas ou upgrades de planos deve ser validada no domínio do cliente, mas sem acoplamento com sistemas externos de crédito (ex: Serasa, SPC) ou sistemas de rede.Regras de Negócio (Invariantes):Para que um cliente seja considerado elegível a uma nova oferta de alto valor, ele deve possuir um score interno de adimplência "Verde" e não ter contestações de fatura abertas nos últimos 6 meses.Mapeamento DDD: A infraestrutura ou a camada de aplicação buscará o score de crédito externo e o injetará em um Domain Service (ex: ServicoValidacaoElegibilidade). Este serviço passará as informações limpas para o método cliente.VerificarElegibilidade(scoreCredito).Exposição para Consumidores: O CRM (Vendas) e o Portal (Upgrade de Plano) farão uma chamada ao endpoint /clientes/{id}/elegibilidade. O domínio processa internamente e responde um JSON boleano estruturado com os motivos em caso de recusa. REQ-03: Modularidade de Identidade e Contas de Faturamento (Value Objects e Agregados)Descrição: O sistema deve suportar a visão unificada do cliente (CPF/CNPJ único), mas permitir que ele possua múltiplos endereços de instalação e múltiplas contas de faturamento (ex: cobrança da banda larga em um endereço e do celular em outro).Regras de Negócio (Invariantes):Endereco e DadosContato devem ser tratados como Value Objects (Objetos de Valor). Eles não possuem identidade própria fora do cliente e são imutáveis (qualquer alteração gera uma nova instância validada).A entidade Cliente garante que pelo menos um endereço de instalação principal exista e seja válido na base de dados dos Correios antes de permitir a ativação do cadastro.Exposição para Consumidores: O Portal de Autoatendimento e o CRM consumirão o endpoint /clientes/{id}/perfil-360. O modelo rico consolidará a árvore de sub-entidades (Endereços, Contas de Cobrança vinculadas e Contratos) entregando uma resposta limpa e de alta performance (otimizada via CQRS/Read Models se necessário). REQ-05: Abstração e Proteção de Dados Sensíveis (Camada Anticorrupção / ACL e LGPD)Descrição: O modelo de domínio rico deve proteger dados sensíveis e garantir conformidade com regras de privacidade (LGPD), assegurando que sistemas terceiros ou menos confiáveis não acessem dados restritos.Regras de Negócio (Invariantes):Dados como senhas de roteador do cliente, tokens de autenticação ou dados parciais de pagamento não ficam expostos na entidade principal pública.Exposição para Consumidores: O microsserviço implementará fachadas de visualização baseadas no perfil do consumidor através de DTOs (Data Transfer Objects).O Portal do Cliente acessa dados de perfil via token do próprio usuário. O CRM acessa dados com máscaras de segurança em telefones/documentos para o atendente.O Sistema de Auditoria/Fraude acessa uma rota restrita com criptografia ponta a ponta.

## 2. Fora de escopo (non-goals)

- Este serviço não integra de fato com sistemas externos (Serasa/SPC, Correios, sistema de Billing).
  As portas `domain/ports/out` que representam essas dependências (ex.: consulta de score de crédito,
  validação de endereço nos Correios, status de fatura) ficam definidas como interfaces; a
  implementação real de um adapter de infraestrutura para esses sistemas terceiros está fora do
  escopo desta PoC.
- Este serviço não implementa autenticação/autorização (login, emissão/validação de token JWT,
  controle de sessão) dos consumidores (CRM/Portal). Assume-se que quem chama já está autenticado;
  o "token do próprio usuário" citado no RF-013 é apenas um dado de contexto recebido, não um
  mecanismo de autenticação implementado por este serviço.
- Este serviço não implementa o motor de faturamento/cobrança em si (geração ou cálculo de faturas).
  Ele apenas consome o status de atraso de fatura vindo do contexto de Billing como dado de entrada
  para suas próprias regras (ex.: transição para Suspenso por Inadimplência).

## 3. Persona / Usuário

Consumidores confirmados deste serviço (todos outros sistemas, não usuários finais diretos da API):

- **CRM (atendente):** vendas/elegibilidade de novas ofertas e visualização de dados do cliente com
  máscara de segurança em telefone/documento.
- **Portal de Autoatendimento:** usado pelo cliente final; consome perfil 360, elegibilidade de
  upgrade de plano, e acessa os próprios dados via token do usuário.
- **Sistema de Auditoria/Fraude:** acessa uma rota restrita para dados sensíveis, com criptografia
  ponta a ponta (simulada nesta PoC — ver seção 6).

O contexto de Billing é uma fonte de dados de entrada (status de fatura), não um consumidor da API.

## 4. Requisitos Funcionais

| ID | Requisito |
|---|---|
| RF-001 | (REQ-01) A entidade `Cliente` gerencia autonomamente suas transições de status (Ativo, Suspenso por Inadimplência, Suspenso a Pedido, Cancelado); nenhum consumidor (CRM, Portal) altera o status diretamente por getters/setters. |
| RF-002 | (REQ-01) O `Cliente` só transiciona para Suspenso por Inadimplência se houver ao menos uma fatura com mais de 15 dias de atraso, informada pelo contexto de Billing. |
| RF-003 | (REQ-01) Um `Cliente` no estado Cancelado não pode reativar serviços nem sofrer novas cobranças; deve ser tratado como histórico ou fluxo de reaquisição. |
| RF-004 | (REQ-01) O `Cliente` expõe um método de consulta (`obterAcoesDisponiveis()`) que retorna o estado atual e as ações permitidas naquele momento, para o CRM/Portal renderizarem dinamicamente botões (Reativar, Suspender, Alterar). |
| RF-005 | (REQ-02) A elegibilidade de um `Cliente` a uma nova oferta de alto valor é verificada no domínio via um Domain Service (`ServicoValidacaoElegibilidade`) que recebe o score de crédito externo já buscado/limpo pela camada de aplicação, sem o domínio acoplar-se a Serasa/SPC/rede diretamente. |
| RF-006 | (REQ-02) Um `Cliente` só é elegível a uma oferta de alto valor se possuir score interno de adimplência "Verde" **e** não tiver contestações de fatura abertas nos últimos 6 meses. |
| RF-007 | (REQ-02) O endpoint `/clientes/{id}/elegibilidade` responde um JSON booleano estruturado, incluindo os motivos da recusa quando o cliente não for elegível. |
| RF-008 | (REQ-03) O `Cliente` tem identidade única por CPF/CNPJ, mas suporta múltiplos endereços de instalação e múltiplas contas de faturamento vinculadas (ex.: banda larga e celular cobrados em contas diferentes). |
| RF-009 | (REQ-03) `Endereco` e `DadosContato` são Value Objects imutáveis, sem identidade própria fora do `Cliente`; qualquer alteração gera uma nova instância validada. |
| RF-010 | (REQ-03) O `Cliente` exige a existência de pelo menos um endereço de instalação principal, validado na base de dados dos Correios, antes de permitir a ativação do cadastro. |
| RF-011 | (REQ-03) O endpoint `/clientes/{id}/perfil-360` retorna a árvore consolidada de sub-entidades (endereços, contas de cobrança vinculadas e contratos) em uma resposta única. |
| RF-012 | (REQ-05) Dados sensíveis (senhas de roteador, tokens de autenticação, dados parciais de pagamento) não são expostos na entidade principal pública do `Cliente`. |
| RF-013 | (REQ-05) O serviço expõe fachadas de visualização via DTOs específicos por perfil de consumidor: Portal (dados de perfil via token do próprio usuário), CRM (telefone/documento mascarados), Auditoria/Fraude (rota restrita, criptografia ponta a ponta simulada nesta PoC). |

## 5. Restrições arquiteturais fixas (já decididas pela plataforma — não precisam clarificação)

Estas vieram do formulário do Backstage e **não são negociáveis** por este serviço — são o mesmo
papel que a "constitution" tem no fluxo spec-kit:

| Parâmetro | Valor |
|---|---|
| Framework | spring-boot-4 |
| Banco de dados | postgresql |
| Abordagem arquitetural | hexagonal |
| Pacote base | `br.com.algar.poc.cliente` |
| Modelagem de domínio | DDD (fixo) |
| Metodologia de teste | TDD (fixo) |

As regras de dependência entre camadas estão em
`src/test/java/br/com/algar/poc/cliente/arch/HexagonalArchitectureTest.java`
— esse teste é o oráculo real, não este documento. Detalhes de convenção (onde cada coisa mora,
o que substituir) estão em `CLAUDE.md`.

## 6. Requisitos Não-Funcionais

- **Segurança/LGPD (RF-013):** o mascaramento de telefone/documento para o DTO do CRM deve ser
  implementado de fato. A "criptografia ponta a ponta" da rota de auditoria é simulada/documentada
  nesta PoC (ex.: um placeholder que sinaliza a exigência), já que implementar criptografia real de
  transporte está fora do escopo de uma PoC de modelagem de domínio.
- Nenhum requisito de performance ou disponibilidade específico além do padrão já garantido pela
  plataforma (ArchUnit, pipeline, branch protection).

## 7. Critérios de Aceite (Definition of Done)

- [ ] RF-001: dado um `Cliente` em qualquer estado, quando um consumidor tenta alterar o status via
      getter/setter público, então a operação não está disponível — a transição só ocorre por um
      método de domínio que valida a regra correspondente.
- [ ] RF-002: dado um `Cliente` Ativo sem fatura em atraso, quando o contexto de Billing informa uma
      fatura com mais de 15 dias de atraso, então o `Cliente` transiciona para Suspenso por
      Inadimplência; dado que nenhuma fatura ultrapassa 15 dias, então a transição é rejeitada.
- [ ] RF-003: dado um `Cliente` Cancelado, quando qualquer operação de reativação de serviço ou nova
      cobrança é solicitada, então a operação é rejeitada pelo domínio.
- [ ] RF-004: dado um `Cliente` em um estado específico, quando `obterAcoesDisponiveis()` é chamado,
      então o retorno contém o estado atual e exatamente o conjunto de ações permitidas para aquele
      estado (ex.: Cancelado não retorna "Reativar").
- [ ] RF-005: dado um score de crédito externo já obtido pela camada de aplicação, quando
      `cliente.verificarElegibilidade(scoreCredito)` é chamado, então o domínio decide sem realizar
      nenhuma chamada própria a sistemas externos de crédito ou rede.
- [ ] RF-006: dado um `Cliente` com score interno "Verde" e sem contestação de fatura aberta nos
      últimos 6 meses, quando a elegibilidade a uma oferta de alto valor é verificada, então o
      resultado é elegível; dado que qualquer uma das duas condições falha, então o resultado é não
      elegível.
- [ ] RF-007: dado um `Cliente` não elegível, quando `GET /clientes/{id}/elegibilidade` é chamado,
      então a resposta é um JSON com um booleano de elegibilidade e a lista de motivos da recusa.
- [ ] RF-008: dado um `Cliente` com CPF/CNPJ único, quando são cadastrados múltiplos endereços de
      instalação e múltiplas contas de faturamento, então todos ficam vinculados ao mesmo `Cliente`
      sem duplicar sua identidade.
- [ ] RF-009: dado um `Endereco` ou `DadosContato` existente, quando um valor precisa mudar, então uma
      nova instância validada é criada e a anterior permanece imutável (sem setters que alterem o
      VO original).
- [ ] RF-010: dado um `Cliente` sem nenhum endereço de instalação principal válido nos Correios,
      quando a ativação do cadastro é solicitada, então a ativação é rejeitada; dado um endereço
      principal válido, então a ativação é permitida.
- [ ] RF-011: dado um `Cliente` com endereços, contas de cobrança e contratos cadastrados, quando
      `GET /clientes/{id}/perfil-360` é chamado, então a resposta consolida essas três árvores de
      sub-entidades em um único payload.
- [ ] RF-012: dado um `Cliente` serializado para um consumidor qualquer, quando a entidade principal
      pública é exposta, então senha de roteador, token de autenticação e dados parciais de pagamento
      não aparecem nela.
- [ ] RF-013: dado o mesmo `Cliente`, quando o Portal, o CRM e o Sistema de Auditoria/Fraude acessam
      seus respectivos DTOs, então cada um recebe apenas os campos e o nível de mascaramento
      definidos para seu perfil (Portal: dados próprios via token; CRM: telefone/documento
      mascarados; Auditoria: rota restrita com criptografia ponta a ponta simulada).
- [x] Suíte de testes (TDD + ArchUnit) roda localmente com `mvn test` e passa — já garantido pela
      plataforma, não depende do domínio real.

## 8. Rastreamento

Depois que este arquivo não tiver mais nenhum marcador de pendência: `/plan` gera `plan.md` (mapeando cada RF
para Aggregate/Use Case/Porta/Adapter dentro da estrutura hexagonal), e `/tasks` gera `tasks.md` (uma
tarefa por RF/critério de aceite, ciclo TDD explícito). Ver `CLAUDE.md` para as regras que o agente
segue em cada fase.
