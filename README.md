# Colosseum

Agent Frameworks in the colosseum `/ˌkɑlɪˈsiəm/`

| Java Framework | Version | Version Tracking | Port | Gradient Colors | Color Description |
| -------------- | ------- | ---------------- | ---- | --------------- | ----------------- |
| Spring AI      | 1.1.0   | https://github.com/spring-projects/spring-ai/tags | 50100 | `#16a085 → #27ae60` | 深绿色系 |
| Embabel        | 0.2.0   | https://github.com/embabel/embabel-agent/tags | 50200 | `#667eea → #764ba2` | 紫色系 |
| langchain4j    | 1.8.0   | https://github.com/langchain4j/langchain4j/tags | 50300 | `#9b59b6 → #3498db` | 紫蓝色系 |
| langgraph4j    | 1.7.2   | https://github.com/langgraph4j/langgraph4j/tags | 50400 | `#e74c3c → #f39c12` | 红橙色系 |

| Python Framework | Version | Version Tracking | Port | Gradient Colors | Color Description |
| ---------------- | ------- | ---------------- | ---- | --------------- | ----------------- |
| parlant          | 3.0.3   | https://github.com/emcie-co/parlant/tags | 60100 | `#667eea → #764ba2` | 紫色系 |
| OxyGent          | -       | https://github.com/jd-opensource/OxyGent/tags | 60200 | `#11998e → #38ef7d` | 青绿色系 |
| langchain        | 1.0.3   | https://github.com/langchain-ai/langchain/tags | 60300 | `#f093fb → #f5576c` | 粉红色系 |
| langgraph        | 1.0.3   | https://github.com/langchain-ai/langgraph/tags | 60400 | `#4facfe → #00f2fe` | 蓝色系 |
| autogen          | 0.7.5   | https://github.com/microsoft/autogen/tags | 60500 | `#fa709a → #fee140` | 粉黄色系 |
| ag2              | 0.10.0  | https://github.com/ag2ai/ag2/tags | 60600 | `#30cfd0 → #330867` | 青紫色系 |
| crewAI           | 1.4.1   | https://github.com/crewAIInc/crewAI/tags | 60700 | `#a8edea → #fed6e3` | 淡青粉色系 |
| llamaindex       | 0.14.8  | https://github.com/run-llama/llama_index/tags | 60800 | `#ff9a56 → #ff6a88` | 橙粉色系 |
| agno             | 2.2.11  | https://github.com/agno-agi/agno/tags | 60900 | `#ffecd2 → #fcb69f` | 暖橙色系 |


## Accessing Web Interface

All framework implementations provide a web interface accessible via browser after startup:

| Framework | Start Command | Web Interface URL |
| --------- | ------------- | ----------------- |
| Spring AI | `cd spring-ai-colosseo && ./start.bat` (Windows) or `mvn spring-boot:run` | http://localhost:50100 |
| Embabel | `cd embabel-colosseo && ./start.sh` or `mvn spring-boot:run` | http://localhost:50200 |
| langchain4j | `cd langchain4j-colosseo && ./start.sh` or `mvn spring-boot:run` | http://localhost:50300 |
| langgraph4j | `cd langgraph4j-colosseo && ./start.sh` or `mvn spring-boot:run` | http://localhost:50400 |
| parlant | `cd parlant-colosseo && ./start.sh` or `python main.py` | http://localhost:60100 |
| OxyGent | `cd oxygent-colosseo && ./start.sh` or `python main.py` | http://localhost:60200 |
| langchain | `cd langchain-colosseo && ./start.sh` or `python main.py` | http://localhost:60300 |
| langgraph | `cd langgraph-colosseo && ./start.sh` or `python main.py` | http://localhost:60400 |
| autogen | `cd autogen-colosseo && ./start.sh` or `python main.py` | http://localhost:60500 |
| ag2 | `cd ag2-colosseo && ./start.sh` or `python main.py` | http://localhost:60600 |
| crewAI | `cd crewai-colosseo && ./start.sh` or `python main.py` | http://localhost:60700 |
| llamaindex | `cd llamaindex-colosseo && ./start.sh` or `python main.py` | http://localhost:60800 |
| agno | `cd agno-colosseo && ./start.sh` or `python main.py` | http://localhost:60900 |

All web interfaces provide the same functionality:
- Multi-language translation service
- Grammar analysis and visualization
- Supported language list viewing
- Real-time translation result display

## Framework Built-in Management Platforms

Some frameworks provide independent visual management platforms for developing, debugging, and monitoring Agent applications:

### OxyGent Management Platform

**How to Start:**
```bash
pip install oxygent
oxygent-server
```
**Access URL:** http://localhost:7788

**Features:** Agent workflow visual editing, real-time monitoring and debugging, task execution history viewing, configuration management

---

### Agno Playground

Agno provides an interactive development environment called Agno Playground.

**How to Start:**
```bash
pip install agno
agno playground
```
**Access URL:** http://localhost:7777

**Features:** Agent interactive testing, workflow debugging, real-time log viewing

---

### AG2 Studio

AG2 provides a visual development platform called AG2 Studio.

**How to Start:**
```bash
pip install ag2[autogenstudio]
ag2studio ui --port 8081
```
**Access URL:** http://localhost:8081

**Features:** Visual Agent workflow building, real-time debugging, conversation history management

---

### LangGraph Studio

LangGraph provides visual debugging tools (requires LangSmith account).

**How to Start:**
```bash
pip install langgraph-cli
export LANGCHAIN_API_KEY="your-api-key"
langgraph dev
```
**Access URL:** http://localhost:8123

**Features:** Graph structure visualization, state inspection, step tracking

---

**Note:** These platforms are framework built-in independent management systems that run separately from the translation service example projects mentioned above.
