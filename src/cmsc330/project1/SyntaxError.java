/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmsc330.project1;

// CMSC 330
// Project 1
// Duane J. Jarc
// March 25, 2014
// Netbeans under Windows 8

// Class that defines a syntax error

class SyntaxError extends Exception
{
    
    // Constructor that creates a synatx error object given the line number and error

    public SyntaxError(int line, String description)
    {
        super("Line: " + line + " " + description);
    }
}