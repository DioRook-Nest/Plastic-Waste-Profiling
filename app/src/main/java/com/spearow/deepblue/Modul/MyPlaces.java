package com.spearow.deepblue.Modul;

import java.util.ArrayList;
import java.util.List;

public class MyPlaces {

        private List<Candidates> candidates;

        private String next_page_token;

        private String[] html_attributions;

        private List<Results> results=new ArrayList<Results>();

        private String status;



        public List<Candidates> getCandidates ()
        {
            return candidates;
        }

        public void setCandidates (List<Candidates> candidates)
        {
            this.candidates = candidates;
        }

        public String getNext_page_token ()
        {
            return next_page_token;
        }

        public void setNext_page_token (String next_page_token)
        {
            this.next_page_token = next_page_token;
        }

        public String[] getHtml_attributions ()
        {
            return html_attributions;
        }

        public void setHtml_attributions (String[] html_attributions)
        {
            this.html_attributions = html_attributions;
        }

        public List<Results> getResults ()
        {
            return results;
        }

        public void setResults (List<Results> results)
        {
            this.results = results;
        }

        public String getStatus ()
        {
            return status;
        }

        public void setStatus (String status)
        {
            this.status = status;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [candidates = "+candidates+",next_page_token = "+next_page_token+", html_attributions = "+html_attributions+", results = "+results+", status = "+status+"]";
        }
}
