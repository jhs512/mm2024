package com.ll.mm.review;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {
    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "상호명 필수항목입니다.")
    @Size(max = 200)
    private String name;

    @NotEmpty(message = "주소는 필수항목입니다.")
    @Size(max = 200)
    private String address;

    @NotEmpty(message = "상세주소는 필수항목입니다.")
    @Size(max = 200)
    private String addressDetail;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;
}
