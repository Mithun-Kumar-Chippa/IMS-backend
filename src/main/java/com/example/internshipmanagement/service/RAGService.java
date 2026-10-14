package com.example.internshipmanagement.service;

import com.example.internshipmanagement.entity.Internship;
import com.example.internshipmanagement.enums.InternshipStatus;
import com.example.internshipmanagement.repository.InternshipRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RAGService {

    private final InternshipRepository internshipRepository;

    public static class DocumentChunk {
        private final String id;
        private final String title;
        private final String content;
        private final String source;

        public DocumentChunk(String id, String title, String content, String source) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.source = source;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getSource() { return source; }
    }

    public static class RetrievalResult {
        private final DocumentChunk chunk;
        private final double score;

        public RetrievalResult(DocumentChunk chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }

        public DocumentChunk getChunk() { return chunk; }
        public double getScore() { return score; }
    }

    public RAGService(InternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    /**
     * Builds the entire knowledge corpus combining database records and platform policies.
     */
    public List<DocumentChunk> buildKnowledgeCorpus() {
        List<DocumentChunk> corpus = new ArrayList<>();

        // 1. Static Platform Guidelines
        corpus.add(new DocumentChunk(
                "DOC_GUIDE_APPLY",
                "Application Workflow & Eligibility Rules",
                "Students can apply to any approved internship before its stated deadline. " +
                "Each student can only submit one active application per internship opportunity. " +
                "An application requires a cover letter and a PDF resume. " +
                "Application statuses progress through: APPLIED -> UNDER_REVIEW -> SHORTLISTED -> ACCEPTED or REJECTED. " +
                "Applications in APPLIED or UNDER_REVIEW state can be voluntarily withdrawn by the student.",
                "Official Platform Policy Manual"
        ));

        corpus.add(new DocumentChunk(
                "DOC_GUIDE_RESUME",
                "Resume & Cover Letter Best Practices",
                "To maximize selection chances: upload a clean 1-2 page PDF resume highlighting technical projects, GPA, and coursework. " +
                "When drafting your cover letter, explain why your skills match the company's tech stack (e.g., Spring Boot, React, MySQL), " +
                "and articulate how you can contribute to their active development team.",
                "Career Services Handbook"
        ));

        corpus.add(new DocumentChunk(
                "DOC_GUIDE_EVAL",
                "Internship Progress Reports and Final Evaluations",
                "Upon acceptance, an official Internship Record is created. Interns are required to submit weekly progress reports " +
                "detailing tasks completed, hours worked, and challenges faced. " +
                "Company mentors review progress reports, provide constructive feedback, and submit a formal final evaluation " +
                "rating technical skills, soft skills, and punctuality on a 1-5 scale.",
                "Academic Evaluation Framework"
        ));

        // 2. Dynamic Live Database Internships
        List<Internship> activeInternships = internshipRepository.findAll().stream()
                .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
                .collect(Collectors.toList());

        for (Internship i : activeInternships) {
            String companyName = i.getCompany().getFullName();
            if (i.getCompany().getCompanyProfile() != null && i.getCompany().getCompanyProfile().getCompanyName() != null) {
                companyName = i.getCompany().getCompanyProfile().getCompanyName();
            }

            String content = String.format(
                    "Internship Title: %s. Company: %s. Department: %s. Location: %s (%s). " +
                    "Stipend: ₹%.2f per month. Duration: %d weeks. Deadline: %s. Max Openings: %d. " +
                    "Description: %s. Requirements: %s. Responsibilities: %s.",
                    i.getTitle(), companyName, i.getDepartment(), i.getLocation(), i.getWorkMode(),
                    i.getStipend() != null ? i.getStipend() : 0.0,
                    i.getDurationWeeks() != null ? i.getDurationWeeks() : 12,
                    i.getDeadline() != null ? i.getDeadline().toString() : "Open",
                    i.getMaxPositions() != null ? i.getMaxPositions() : 1,
                    i.getDescription(),
                    i.getRequirements() != null ? i.getRequirements() : "Basic degree coursework",
                    i.getResponsibilities() != null ? i.getResponsibilities() : "Software development tasks"
            );

            corpus.add(new DocumentChunk(
                    "INTERNSHIP_" + i.getId(),
                    i.getTitle() + " at " + companyName,
                    content,
                    "Live Database Posting #" + i.getId()
            ));
        }

        return corpus;
    }

    /**
     * Vector retrieval using Term-Frequency Cosine Similarity.
     */
    public List<RetrievalResult> retrieveRelevantContext(String query, int topK) {
        List<DocumentChunk> corpus = buildKnowledgeCorpus();
        if (query == null || query.isBlank() || corpus.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Integer> queryVector = extractTermFrequency(query);
        List<RetrievalResult> scored = new ArrayList<>();

        for (DocumentChunk chunk : corpus) {
            Map<String, Integer> docVector = extractTermFrequency(chunk.getTitle() + " " + chunk.getContent());
            double similarity = computeCosineSimilarity(queryVector, docVector);
            if (similarity > 0.05) {
                scored.add(new RetrievalResult(chunk, similarity));
            }
        }

        scored.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return scored.stream().limit(topK).collect(Collectors.toList());
    }

    private Map<String, Integer> extractTermFrequency(String text) {
        Map<String, Integer> freq = new HashMap<>();
        String[] tokens = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+");
        Set<String> stopWords = Set.of(
                "a", "an", "the", "and", "or", "in", "on", "at", "to", "for", "with",
                "is", "are", "was", "were", "of", "it", "this", "that", "from", "by"
        );

        for (String token : tokens) {
            if (token.length() > 2 && !stopWords.contains(token)) {
                freq.put(token, freq.getOrDefault(token, 0) + 1);
            }
        }
        return freq;
    }

    private double computeCosineSimilarity(Map<String, Integer> v1, Map<String, Integer> v2) {
        if (v1.isEmpty() || v2.isEmpty()) return 0.0;

        double dotProduct = 0.0;
        for (Map.Entry<String, Integer> entry : v1.entrySet()) {
            if (v2.containsKey(entry.getKey())) {
                dotProduct += entry.getValue() * v2.get(entry.getKey());
            }
        }

        double norm1 = 0.0;
        for (int count : v1.values()) {
            norm1 += count * count;
        }

        double norm2 = 0.0;
        for (int count : v2.values()) {
            norm2 += count * count;
        }

        if (norm1 == 0.0 || norm2 == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
