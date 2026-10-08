package com.example.TicketManagement.Entity;



import jakarta.persistence.*;



import java.time.LocalDateTime;



@Entity

public class ServiceRequest {



    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long ticketId;



    private String issue;



    private String issueDescription;



    private String priority;



    private String status;



    private String assignedTo;



    private String resolutionDetails;



    private LocalDateTime createdAt;



    @Lob

    private byte[] attachment;



    private String attachmentFileName;



    public ServiceRequest() {

    }



    public ServiceRequest(String issue, String issueDescription,

                          String priority, String status) {

        this.issue = issue;

        this.issueDescription = issueDescription;

        this.priority = priority;

        this.status = status;

    }



    @PrePersist

    public void setCreationTime() {

        if (createdAt == null) {

            createdAt = LocalDateTime.now();

        }

    }



    public Long getTicketId() {

        return ticketId;

    }



    public void setTicketId(Long ticketId) {

        this.ticketId = ticketId;

    }



    public String getIssue() {

        return issue;

    }



    public void setIssue(String issue) {

        this.issue = issue;

    }



    public String getIssueDescription() {

        return issueDescription;

    }



    public void setIssueDescription(String issueDescription) {

        this.issueDescription = issueDescription;

    }



    public String getPriority() {

        return priority;

    }



    public void setPriority(String priority) {

        this.priority = priority;

    }



    public String getStatus() {

        return status;

    }



    public void setStatus(String status) {

        this.status = status;

    }



    public String getAssignedTo() {

        return assignedTo;

    }



    public void setAssignedTo(String assignedTo) {

        this.assignedTo = assignedTo;

    }



    public String getResolutionDetails() {

        return resolutionDetails;

    }



    public void setResolutionDetails(String resolutionDetails) {

        this.resolutionDetails = resolutionDetails;

    }



    public LocalDateTime getCreatedAt() {

        return createdAt;

    }



    public void setCreatedAt(LocalDateTime createdAt) {

        this.createdAt = createdAt;

    }



    public byte[] getAttachment() {

        return attachment;

    }



    public void setAttachment(byte[] attachment) {

        this.attachment = attachment;

    }



    public String getAttachmentFileName() {

        return attachmentFileName;

    }



    public void setAttachmentFileName(String attachmentFileName) {

        this.attachmentFileName = attachmentFileName;

    }



    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "id") // Links to the User table's primary key

    private User user;



    public User getUser() {

        return user;

    }



    public void setUser(User user) {

        this.user = user;

    }

}