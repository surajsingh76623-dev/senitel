package com.sentinel.dto;

import java.util.List;

public class ClarificationAnswerRequest {

    private List<ClarificationEntry> responses;

    public ClarificationAnswerRequest() {}

    public ClarificationAnswerRequest(List<ClarificationEntry> responses) {
        this.responses = responses;
    }

    public List<ClarificationEntry> getResponses() { return responses; }
    public void setResponses(List<ClarificationEntry> responses) { this.responses = responses; }

    public static class ClarificationEntry {
        private String question;
        private String answer;

        public ClarificationEntry() {}

        public ClarificationEntry(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }
}
