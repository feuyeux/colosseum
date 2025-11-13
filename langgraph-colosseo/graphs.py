from langgraph.graph import StateGraph, END
from langchain.llms import Ollama
from langchain.prompts import PromptTemplate
from typing import TypedDict, Annotated, Sequence, Optional
from langchain_core.messages import BaseMessage, HumanMessage, SystemMessage
import logging

logger = logging.getLogger(__name__)

# Define state types
class TranslationState(TypedDict):
    text: str
    source_language: str
    target_language: str
    context: Optional[str]
    translated_text: str
    error: Optional[str]

class AnalysisState(TypedDict):
    text: str
    language: str
    analysis: str
    components: list
    error: Optional[str]

class TranslationGraph:
    def __init__(self, ollama_client):
        self.ollama_client = ollama_client
        self.llm = Ollama(
            base_url="http://localhost:11434",
            model="qwen2.5:latest"
        )
        self.graph = self._create_translation_graph()
    
    def _create_translation_graph(self):
        """Create the translation workflow graph"""
        
        def validate_input(state: TranslationState) -> TranslationState:
            """Validate input parameters"""
            if not state["text"] or not state["text"].strip():
                state["error"] = "Text cannot be empty"
                return state
            
            if state["source_language"] == state["target_language"]:
                state["error"] = "Source and target languages cannot be the same"
                return state
            
            return state
        
        def translate_text(state: TranslationState) -> TranslationState:
            """Translate text using LLM"""
            try:
                if state.get("error"):
                    return state
                
                system_prompt = f"""You are a professional translator. Translate the given text from {state['source_language']} to {state['target_language']}.
                Provide only the translation without any additional explanation or commentary. Ensure the translation is accurate and natural-sounding."""
                
                user_prompt = f"Text to translate: {state['text']}"
                if state.get("context"):
                    user_prompt += f"\nContext: {state['context']}"
                
                messages = [
                    SystemMessage(content=system_prompt),
                    HumanMessage(content=user_prompt)
                ]
                
                response = self.llm.invoke(messages)
                state["translated_text"] = response.strip()
                
            except Exception as e:
                logger.error(f"Translation error: {e}")
                state["error"] = f"Translation failed: {str(e)}"
            
            return state
        
        def format_output(state: TranslationState) -> TranslationState:
            """Format the final output"""
            if not state.get("translated_text") and not state.get("error"):
                state["error"] = "Translation produced no result"
            
            return state
        
        # Build the graph
        workflow = StateGraph(TranslationState)
        
        # Add nodes
        workflow.add_node("validate", validate_input)
        workflow.add_node("translate", translate_text)
        workflow.add_node("format", format_output)
        
        # Add edges
        workflow.add_edge("validate", "translate")
        workflow.add_edge("translate", "format")
        workflow.add_edge("format", END)
        
        # Set entry point
        workflow.set_entry_point("validate")
        
        return workflow.compile()
    
    def translate(self, text: str, source_language: str, target_language: str, context: Optional[str] = None) -> str:
        """Execute translation graph"""
        initial_state = {
            "text": text,
            "source_language": source_language,
            "target_language": target_language,
            "context": context,
            "translated_text": "",
            "error": None
        }
        
        result = self.graph.invoke(initial_state)
        
        if result.get("error"):
            raise Exception(result["error"])
        
        return result["translated_text"]

class AnalysisGraph:
    def __init__(self, ollama_client):
        self.ollama_client = ollama_client
        self.llm = Ollama(
            base_url="http://localhost:11434",
            model="qwen2.5:latest"
        )
        self.graph = self._create_analysis_graph()
    
    def _create_analysis_graph(self):
        """Create the analysis workflow graph"""
        
        def validate_input(state: AnalysisState) -> AnalysisState:
            """Validate input parameters"""
            if not state["text"] or not state["text"].strip():
                state["error"] = "Text cannot be empty"
                return state
            
            return state
        
        def analyze_grammar(state: AnalysisState) -> AnalysisState:
            """Analyze grammar using LLM"""
            try:
                if state.get("error"):
                    return state
                
                system_prompt = f"""You are a linguistic expert. Analyze the grammatical components of the given {state['language']} text.
                Identify parts of speech, sentence structure, and grammatical relationships.
                Provide a detailed analysis focusing on the main grammatical components."""
                
                user_prompt = f"Text to analyze: {state['text']}"
                
                messages = [
                    SystemMessage(content=system_prompt),
                    HumanMessage(content=user_prompt)
                ]
                
                response = self.llm.invoke(messages)
                state["analysis"] = response.strip()
                
            except Exception as e:
                logger.error(f"Analysis error: {e}")
                state["error"] = f"Grammar analysis failed: {str(e)}"
            
            return state
        
        def parse_components(state: AnalysisState) -> AnalysisState:
            """Parse grammar components from analysis"""
            if state.get("error") or not state.get("analysis"):
                return state
            
            # Simple parsing - in a real implementation, you'd want more sophisticated parsing
            components = []
            analysis_text = state["analysis"].lower()
            
            component_types = [
                ("noun", "#FF6B6B"),
                ("verb", "#4ECDC4"),
                ("adjective", "#45B7D1"),
                ("adverb", "#96CEB4"),
                ("preposition", "#FFEAA7"),
                ("conjunction", "#DDA0DD"),
                ("pronoun", "#FFB6C1"),
                ("article", "#98FB98")
            ]
            
            position = 0
            for comp_type, color in component_types:
                if comp_type in analysis_text:
                    components.append({
                        "component": comp_type,
                        "type": comp_type,
                        "explanation": comp_type.capitalize(),
                        "color": color,
                        "position": position,
                        "length": len(comp_type)
                    })
                    position += 1
            
            state["components"] = components
            return state
        
        def format_output(state: AnalysisState) -> AnalysisState:
            """Format the final output"""
            if not state.get("analysis") and not state.get("error"):
                state["error"] = "Analysis produced no result"
            
            return state
        
        # Build the graph
        workflow = StateGraph(AnalysisState)
        
        # Add nodes
        workflow.add_node("validate", validate_input)
        workflow.add_node("analyze", analyze_grammar)
        workflow.add_node("parse", parse_components)
        workflow.add_node("format", format_output)
        
        # Add edges
        workflow.add_edge("validate", "analyze")
        workflow.add_edge("analyze", "parse")
        workflow.add_edge("parse", "format")
        workflow.add_edge("format", END)
        
        # Set entry point
        workflow.set_entry_point("validate")
        
        return workflow.compile()
    
    def analyze(self, text: str, language: str) -> str:
        """Execute analysis graph"""
        initial_state = {
            "text": text,
            "language": language,
            "analysis": "",
            "components": [],
            "error": None
        }
        
        result = self.graph.invoke(initial_state)
        
        if result.get("error"):
            raise Exception(result["error"])
        
        return result["analysis"]