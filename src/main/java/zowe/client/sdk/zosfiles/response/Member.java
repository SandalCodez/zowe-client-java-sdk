/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package zowe.client.sdk.zosfiles.response;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Represents a z/OS partition data set member
 *
 * @author Frank Giordano
 * @version 1.0
 */
public class Member {

    /**
     * The name of the member
     */
    private final Optional<String> member;

    /**
     * The version of member
     */
    private final OptionalInt vers;

    /**
     * The number of modifications of member
     */
    private final OptionalInt mod;

    /**
     * The creation date of member
     */
    private final Optional<String> c4date;

    /**
     * The modification date of member
     */
    private final Optional<String> m4date;

    /**
     * The cnorc of member
     */
    private final OptionalInt cnorc;

    /**
     * The inorc of member
     */
    private final OptionalInt inorc;

    /**
     * The mnorc of member
     */
    private final OptionalInt mnorc;

    /**
     * The mtime of member
     */
    private final Optional<String> mtime;

    /**
     * The msec of member
     */
    private final Optional<String> msec;

    /**
     * The user of member
     */
    private final Optional<String> user;

    /**
     * The sclm of member
     */
    private final Optional<String> sclm;

    private Member(Member.Builder builder) {
        this.member = Optional.ofNullable(builder.member);
        if (builder.vers == null) {
            this.vers = OptionalInt.empty();
        } else {
            this.vers = OptionalInt.of(builder.vers);
        }
        if (builder.mod == null) {
            this.mod = OptionalInt.empty();
        } else {
            this.mod = OptionalInt.of(builder.mod);
        }
        this.c4date = Optional.ofNullable(builder.c4date);
        this.m4date = Optional.ofNullable(builder.m4date);
        if (builder.cnorc == null) {
            this.cnorc = OptionalInt.empty();
        } else {
            this.cnorc = OptionalInt.of(builder.cnorc);
        }
        if (builder.inorc == null) {
            this.inorc = OptionalInt.empty();
        } else {
            this.inorc = OptionalInt.of(builder.inorc);
        }
        if (builder.mnorc == null) {
            this.mnorc = OptionalInt.empty();
        } else {
            this.mnorc = OptionalInt.of(builder.mnorc);
        }
        this.mtime = Optional.ofNullable(builder.mtime);
        this.msec = Optional.ofNullable(builder.msec);
        this.user = Optional.ofNullable(builder.user);
        this.sclm = Optional.ofNullable(builder.sclm);
    }

    public Optional<String> getMember() {
        return member;
    }

    public OptionalInt getVers() {
        return vers;
    }

    public OptionalInt getMod() {
        return mod;
    }

    public Optional<String> getC4date() {
        return c4date;
    }

    public Optional<String> getM4date() {
        return m4date;
    }

    public OptionalInt getCnorc() {
        return cnorc;
    }

    public OptionalInt getInorc() {
        return inorc;
    }

    public OptionalInt getMnorc() {
        return mnorc;
    }

    public Optional<String> getMtime() {
        return mtime;
    }

    public Optional<String> getMsec() {
        return msec;
    }

    public Optional<String> getUser() {
        return user;
    }

    public Optional<String> getSclm() {
        return sclm;
    }

    @Override
    public String toString() {
        return "Member{" +
                "member=" + member +
                ", vers=" + vers +
                ", mod=" + mod +
                ", c4date=" + c4date +
                ", m4date=" + m4date +
                ", cnorc=" + cnorc +
                ", inorc=" + inorc +
                ", mnorc=" + mnorc +
                ", mtime=" + mtime +
                ", msec=" + msec +
                ", user=" + user +
                ", sclm=" + sclm +
                '}';
    }

    public static class Builder {

        private String member;
        private Integer vers;
        private Integer mod;
        private String c4date;
        private String m4date;
        private Integer cnorc;
        private Integer inorc;
        private Integer mnorc;
        private String mtime;
        private String msec;
        private String user;
        private String sclm;

        public Member.Builder member(String member) {
            this.member = member;
            return this;
        }

        public Member.Builder vers(Integer vers) {
            this.vers = vers;
            return this;
        }

        public Member.Builder mod(Integer mod) {
            this.mod = mod;
            return this;
        }

        public Member.Builder c4date(String c4date) {
            this.c4date = c4date;
            return this;
        }

        public Member.Builder m4date(String m4date) {
            this.m4date = m4date;
            return this;
        }

        public Member.Builder cnorc(Integer cnorc) {
            this.cnorc = cnorc;
            return this;
        }

        public Member.Builder inorc(Integer inorc) {
            this.inorc = inorc;
            return this;
        }

        public Member.Builder mnorc(Integer mnorc) {
            this.mnorc = mnorc;
            return this;
        }

        public Member.Builder mtime(String mtime) {
            this.mtime = mtime;
            return this;
        }

        public Member.Builder msec(String msec) {
            this.msec = msec;
            return this;
        }

        public Member.Builder user(String user) {
            this.user = user;
            return this;
        }

        public Member.Builder sclm(String sclm) {
            this.sclm = sclm;
            return this;
        }

        public Member build() {
            return new Member(this);
        }

    }

}

